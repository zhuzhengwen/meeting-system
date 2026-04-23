-- =====================================================================
-- 补丁：新增 会议查询 菜单（已有数据库执行此脚本）
-- =====================================================================

-- 1. 调整原有菜单排序：跟踪事项 5→6，会议室管理 6→7
UPDATE sys_menu SET order_num = 6 WHERE menu_id = 2105;
UPDATE sys_menu SET order_num = 7 WHERE menu_id = 2106;

-- 2. 插入会议查询菜单（排第 5 位，仅管理员可见）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark)
VALUES (2107, '会议查询', 2100, 5, 'search', 'mms/search/index',
  1, 0, 'C', 0, 0, 'mms:meeting:query', 'search',
  'admin', now(), '', null, '会议查询（管理员）');

-- 3. 更新序列
SELECT setval('sys_menu_menu_id_seq', (SELECT MAX(menu_id) FROM sys_menu));
