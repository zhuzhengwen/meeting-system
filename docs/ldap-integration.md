# LDAP 接入说明

## 概述

系统支持 **域账号（LDAP/AD）** 与 **本地账号** 双模式登录。启用 LDAP 后：

- 用户使用域账号密码登录，密码由 LDAP 服务器校验，系统不存储域用户密码
- 首次登录自动按用户所在 OU 结构创建部门，并分配默认角色（JIT 开通）
- 后续登录若用户 OU 有变动，自动同步部门归属

---

## 一、前置条件

| 条件 | 说明 |
|------|------|
| LDAP/AD 服务可访问 | 确保应用服务器能通过网络访问 LDAP 服务器（默认端口 389，LDAPS 636） |
| 只读服务账号 | 需在 AD/LDAP 中创建一个只读账号用于查询用户信息 |
| 父级部门存在 | `sys_dept` 表中需存在一个用于挂载 LDAP 部门的父节点（默认 ID=100） |
| 默认角色存在 | `sys_role` 表中需有 LDAP 用户首次登录时分配的角色（默认 ID=2） |

---

## 二、配置项说明

编辑 `ruoyi-admin/src/main/resources/application.yml`，找到 `ldap:` 节点：

```yaml
ldap:
  enabled: true                              # 改为 true 以启用 LDAP 登录

  url: ldap://192.168.1.10:389              # LDAP 服务器地址，LDAPS 用 ldaps://
  base: dc=company,dc=com                   # 搜索根节点，对应公司域名

  userDn: cn=readonly,dc=company,dc=com     # 只读服务账号 DN
  password: your_readonly_password          # 只读服务账号密码

  # 用户搜索过滤器
  # Active Directory 使用 sAMAccountName（登录名）
  # OpenLDAP 使用 uid
  userSearchFilter: (sAMAccountName={0})

  # LDAP 属性映射（左边是系统字段，右边是 LDAP 属性名）
  attrs:
    department: department                  # 部门属性（AD 通常为 department）
    displayName: displayName                # 显示名/姓名
    email: mail                             # 邮箱（AD 为 mail，OpenLDAP 可能为 email）

  defaultDeptParentId: 100                  # LDAP 部门挂载的父节点 dept_id
  defaultRoleId: 2                          # 首次登录自动分配的角色 ID
```

### 常见 LDAP 类型参考

| 类型 | `base` 示例 | `userSearchFilter` |
|------|------------|-------------------|
| Windows AD | `dc=corp,dc=example,dc=com` | `(sAMAccountName={0})` |
| OpenLDAP | `dc=example,dc=com` | `(uid={0})` |
| 混合 | 视实际而定 | `(|(sAMAccountName={0})(uid={0}))` |

---

## 三、接入步骤

### 1. 向 IT/AD 管理员申请

- LDAP 服务器地址和端口
- 只读查询账号（DN + 密码）
- 确认域的 Base DN（如 `dc=corp,dc=com`）
- 确认用户登录属性名（`sAMAccountName` 还是 `uid`）
- 确认显示名、邮箱对应的 LDAP 属性名

### 2. 配置 application.yml

按第二节填写所有 `ldap:` 配置项，将 `enabled` 改为 `true`。

### 3. 在系统中准备父级部门

登录系统 → **系统管理 → 部门管理**，确认或新建一个用于承接 LDAP 部门的父节点。记录其 `dept_id`，填入 `defaultDeptParentId`。

例如：在"公司"节点下新建"域账号用户"节点，`dept_id` 为 `105`，则配置：
```yaml
defaultDeptParentId: 105
```

### 4. 在系统中准备默认角色

登录系统 → **系统管理 → 角色管理**，确认给 LDAP 用户分配的角色（通常为普通员工角色，数据权限建议设为"本部门"）。记录 `role_id`，填入 `defaultRoleId`。

### 5. 重启服务

```bash
# Maven 打包
mvn clean package -DskipTests

# 或直接重启 Spring Boot 应用
```

### 6. 验证

在登录页切换到「域账号登录」tab，输入域账号和密码，登录成功即表示接入完成。

首次登录后可在 **系统管理 → 用户管理** 中看到自动创建的用户，部门树下也会出现对应 OU 的部门节点。

---

## 四、认证流程说明

```
用户输入域账号密码
       ↓
LDAP Bind 验证密码（密码不进系统数据库）
       ↓
读取用户 displayName / mail / OU 路径
       ↓
按 OU 链逐级查找或创建 sys_dept 部门节点
       ↓
查找 sys_user：不存在则自动创建并绑定默认角色
              已存在则检查部门是否变化，有变化则同步
       ↓
生成 JWT Token，与本地账号登录完全相同的权限体系
```

---

## 五、常见问题

**Q：域账号用户能修改密码吗？**
不能，密码由 LDAP 服务器管理，系统不存储也不修改域用户密码。

**Q：LDAP 用户和本地用户可以共存吗？**
可以。系统通过 `sys_user.login_type` 字段区分：`0` 为本地账号，`1` 为 LDAP 账号。

**Q：用户 OU 变动后什么时候同步？**
下次登录时自动同步。如需立即同步，可在用户管理页手动修改部门。

**Q：连接 LDAP 超时/失败怎么办？**
检查：① 服务器地址和端口是否正确；② 防火墙是否开放 389/636 端口；③ 服务账号 DN 和密码是否正确；④ 查看后端日志中的 `LDAP服务连接失败` 错误信息。

**Q：启用 LDAPS（SSL）怎么配置？**
将 `url` 改为 `ldaps://your-server:636`，如果使用自签名证书还需在 JVM 中导入证书：
```bash
keytool -import -alias ldap-cert -keystore $JAVA_HOME/lib/security/cacerts \
  -file your-ldap-cert.cer -storepass changeit
```

**Q：搜索用户时提示"PartialResultException"？**
AD 环境常见，系统已配置 `setIgnorePartialResultException(true)` 自动忽略，通常不影响使用。

---

## 六、属性映射排查

如果用户姓名或邮箱读取为空，可用 LDAP 浏览器工具（如 Apache Directory Studio）连接 AD，查看用户对象的实际属性名，然后修改 `attrs` 映射：

```yaml
attrs:
  displayName: cn          # 如果 displayName 为空，尝试 cn
  email: userPrincipalName # 如果 mail 为空，尝试 userPrincipalName
```

---

*如有问题请联系系统管理员或查看后端日志。*
