-- LDAP登录支持补丁
-- 执行前确认数据库：sys_user 表存在

-- 1. sys_user 增加 login_type 字段
ALTER TABLE sys_user
    ADD COLUMN IF NOT EXISTS login_type char(1) NOT NULL DEFAULT '0';
COMMENT ON COLUMN sys_user.login_type IS '登录方式（0系统账号 1LDAP账号）';

-- 2. 在 sys_dept 下预置"LDAP部门"根节点
--    parent_id=0 表示顶级，dept_id 需与 application.yml ldap.defaultDeptParentId 一致
--    如果已有合适的父节点（如 100），可跳过此步，直接在 yml 里配置对应 ID
INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, status, del_flag, create_by, create_time)
SELECT 200, 0, '0', 'LDAP部门', 99, '0', '0', 'admin', now()
WHERE NOT EXISTS (SELECT 1 FROM sys_dept WHERE dept_id = 200);

-- 3. 预置 LDAP 用户默认角色（data_scope=3 表示本部门权限）
--    role_id 需与 application.yml ldap.defaultRoleId 一致
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
SELECT 10, 'LDAP默认角色', 'ldap_user', 10, '3', true, true, '0', '0', 'admin', now(), 'LDAP用户自动分配角色，数据权限=本部门'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_id = 10);

-- 4. 给 LDAP 默认角色分配基础菜单权限（按需调整 menu_id 范围）
--    此处示例：分配所有 status='0' 的非按钮菜单（menu_type != 'F'）
--    生产环境建议手动在系统界面配置
-- INSERT INTO sys_role_menu (role_id, menu_id)
-- SELECT 10, menu_id FROM sys_menu WHERE status = '0' AND menu_type != 'F'
-- ON CONFLICT DO NOTHING;
