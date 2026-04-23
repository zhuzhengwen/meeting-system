package com.ruoyi.framework.web.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;

/**
 * LDAP 登录服务
 * 认证流程：LDAP验密 → 按OU链找/建部门树 → 找/建系统用户 → 返回Token
 */
@Component
@ConditionalOnProperty(name = "ldap.enabled", havingValue = "true")
public class LdapLoginService
{
    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysPermissionService permissionService;

    @Value("${ldap.userSearchFilter}")
    private String userSearchFilter;

    @Value("${ldap.attrs.department:department}")
    private String attrDepartment;

    @Value("${ldap.attrs.displayName:displayName}")
    private String attrDisplayName;

    @Value("${ldap.attrs.email:mail}")
    private String attrEmail;

    @Value("${ldap.defaultDeptParentId}")
    private Long defaultDeptParentId;

    @Value("${ldap.defaultRoleId}")
    private Long defaultRoleId;

    /**
     * LDAP 登录入口
     */
    public String login(String username, String password)
    {
        if (StringUtils.isAnyBlank(username, password))
        {
            throw new CustomException("用户名或密码不能为空");
        }

        // 1. LDAP 验密并读取用户属性
        LdapUserInfo ldapInfo = authenticateAndFetch(username, password);

        // 2. 根据 OU 链找或建部门树，返回叶子节点 dept_id
        Long deptId = findOrCreateDeptChain(ldapInfo.ouChain);

        // 3. 找或自动开通系统用户
        SysUser user = findOrProvisionUser(username, ldapInfo, deptId);

        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, "LDAP登录成功"));

        // 4. 构建 Token（与普通账号完全相同的权限体系）
        LoginUser loginUser = new LoginUser(user.getUserId(), user.getDeptId(),
                user, permissionService.getMenuPermission(user));
        return tokenService.createToken(loginUser);
    }

    /**
     * LDAP Bind 验密，同时读取 displayName / mail / OU 链
     * 验证失败直接抛异常
     */
    private LdapUserInfo authenticateAndFetch(String username, String password)
    {
        // 构造搜索条件，把 {0} 替换为用户名
        String filter = userSearchFilter.replace("{0}", username);
        // 取过滤器中的属性名，例如 (sAMAccountName={0}) → sAMAccountName
        String attr = filter.replaceAll("[()=].*", "").replaceAll("[()=]", "").trim();
        if (attr.startsWith("(")) attr = attr.substring(1);

        LdapQuery query = LdapQueryBuilder.query().filter(filter);
        try
        {
            return ldapTemplate.authenticate(query, password, (ctx, id) -> {
                LdapUserInfo info = new LdapUserInfo();
                info.displayName = getAttr(ctx, attrDisplayName, username);
                info.email       = getAttr(ctx, attrEmail, "");
                info.ouChain     = parseOuChain(id.getAbsoluteName().toString());
                return info;
            });
        }
        catch (AuthenticationException e)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, "LDAP账号或密码错误"));
            throw new CustomException("账号或密码错误");
        }
        catch (Exception e)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, "LDAP服务连接失败：" + e.getMessage()));
            throw new CustomException("LDAP服务连接失败，请联系管理员");
        }
    }

    /**
     * 从 DN 提取 OU 列表，顺序从顶层（最通用）到叶子（最具体）
     * 例如 CN=张三,OU=研发一组,OU=研发中心,DC=company,DC=com → ["研发中心","研发一组"]
     */
    private List<String> parseOuChain(String dn)
    {
        List<String> ous = new ArrayList<>();
        try
        {
            LdapName ldapName = new LdapName(dn);
            // LdapName.getRdns() 顺序：右→左（DC在前，CN在末尾）
            // 遍历得到的 OU 顺序即为从上到下
            for (Rdn rdn : ldapName.getRdns())
            {
                if ("OU".equalsIgnoreCase(rdn.getType()))
                {
                    ous.add(rdn.getValue().toString());
                }
            }
        }
        catch (InvalidNameException e)
        {
            // DN 解析失败时忽略，部门将挂在根节点下
        }
        return ous;
    }

    /**
     * 按 OU 链递归找或建 sys_dept，返回叶子节点 dept_id
     * 加 synchronized 防止同部门多用户并发首次登录时重复创建
     */
    @Transactional
    public synchronized Long findOrCreateDeptChain(List<String> ouChain)
    {
        if (ouChain == null || ouChain.isEmpty())
        {
            return defaultDeptParentId;
        }

        Long parentId = defaultDeptParentId;
        SysDept parentDept = deptService.selectDeptById(parentId);
        // ancestors 例如 "0,100" 表示根→父
        String parentAncestors = parentDept != null ? parentDept.getAncestors() : "0";

        for (String ouName : ouChain)
        {
            SysDept dept = deptService.selectDeptByNameAndParent(ouName, parentId);
            if (dept == null)
            {
                dept = new SysDept();
                dept.setParentId(parentId);
                dept.setDeptName(ouName);
                dept.setAncestors(parentAncestors + "," + parentId);
                dept.setOrderNum("0");
                dept.setStatus("0");
                dept.setCreateBy("ldap-auto");
                deptService.insertDept(dept);
            }
            parentAncestors = dept.getAncestors();
            parentId = dept.getDeptId();
        }
        return parentId;
    }

    /**
     * 查系统用户：不存在则 JIT 创建并绑定默认角色；存在则同步部门变更
     */
    @Transactional
    public SysUser findOrProvisionUser(String username, LdapUserInfo info, Long deptId)
    {
        SysUser user = userService.selectUserByUserName(username);
        if (user == null)
        {
            user = new SysUser();
            user.setUserName(username);
            user.setNickName(StringUtils.defaultIfBlank(info.displayName, username));
            user.setEmail(StringUtils.defaultString(info.email, ""));
            user.setDeptId(deptId);
            user.setLoginType("1");
            user.setPassword("");
            user.setStatus("0");
            user.setCreateBy("ldap-auto");
            userService.insertUser(user);

            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(defaultRoleId);
            userRoleMapper.batchUserRole(Collections.singletonList(userRole));
        }
        else if (!deptId.equals(user.getDeptId()))
        {
            // LDAP 里部门有变化，同步更新
            user.setDeptId(deptId);
            userService.updateUser(user);
        }
        return user;
    }

    private String getAttr(javax.naming.directory.DirContext ctx, String attrName, String defaultVal)
    {
        try
        {
            javax.naming.directory.Attributes attrs = ctx.getAttributes("", new String[]{ attrName });
            javax.naming.directory.Attribute a = attrs.get(attrName);
            return a != null ? (String) a.get() : defaultVal;
        }
        catch (Exception e)
        {
            return defaultVal;
        }
    }

    static class LdapUserInfo
    {
        String displayName;
        String email;
        List<String> ouChain;
    }
}
