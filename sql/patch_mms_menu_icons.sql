-- 更新 MMS 菜单图标为专属会议系统图标
-- 对应 src/assets/icons/svg/ 下的 SVG 文件名

UPDATE sys_menu SET icon = 'date'         WHERE menu_id = 2100; -- 会议管理（目录）
UPDATE sys_menu SET icon = 'dashboard'    WHERE menu_id = 2101; -- 仪表盘（保持）
UPDATE sys_menu SET icon = 'mms-schedule' WHERE menu_id = 2102; -- 会议排程
UPDATE sys_menu SET icon = 'mms-booking'  WHERE menu_id = 2103; -- 预约会议
UPDATE sys_menu SET icon = 'mms-meeting'  WHERE menu_id = 2104; -- 会议记录
UPDATE sys_menu SET icon = 'search'       WHERE menu_id = 2107; -- 会议查询（保持）
UPDATE sys_menu SET icon = 'mms-tracking' WHERE menu_id = 2105; -- 跟踪事项
UPDATE sys_menu SET icon = 'mms-room'     WHERE menu_id = 2106; -- 会议室管理
