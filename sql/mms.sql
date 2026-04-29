-- =====================================================================
-- 会议管理系统 (MMS) 数据库脚本
-- 使用方法：在 RuoYi PostgreSQL 数据库中执行此脚本
-- =====================================================================

-- ----------------------------
-- 菜单配置（会议管理模块）
-- ----------------------------
-- 一级目录：会议管理
insert into sys_menu values(2100, '会议管理', 0, 5, 'mms', '', 1, 0, 'M', 0, 0, '', 'date', 'admin', now(), '', null, '会议管理系统目录');
-- 二级菜单
insert into sys_menu values(2101, '仪表盘',   2100, 1, 'dashboard', 'mms/dashboard/index', 1, 0, 'C', 0, 0, 'mms:dashboard:view', 'dashboard', 'admin', now(), '', null, '会议仪表盘');
insert into sys_menu values(2102, '会议排程', 2100, 2, 'schedule',  'mms/schedule/index',  1, 0, 'C', 0, 0, 'mms:schedule:view',  'tree',      'admin', now(), '', null, '会议排程时间线');
insert into sys_menu values(2103, '预约会议', 2100, 3, 'booking',   'mms/booking/index',   1, 0, 'C', 0, 0, 'mms:meeting:add',    'edit',      'admin', now(), '', null, '预约会议向导');
insert into sys_menu values(2104, '会议记录', 2100, 4, 'meeting',   'mms/meeting/index',   1, 0, 'C', 0, 0, 'mms:meeting:list',   'list',      'admin', now(), '', null, '会议记录列表');
insert into sys_menu values(2107, '会议查询', 2100, 5, 'search',    'mms/search/index',    1, 0, 'C', 0, 0, 'mms:meeting:query',  'search',    'admin', now(), '', null, '会议查询（管理员）');
insert into sys_menu values(2105, '跟踪事项', 2100, 6, 'tracking',  'mms/tracking/index',  1, 0, 'C', 0, 0, 'mms:tracking:list', 'checkbox',  'admin', now(), '', null, '会议跟踪事项');
insert into sys_menu values(2106, '会议室管理', 2100, 7, 'room',    'mms/room/index',      1, 0, 'C', 0, 0, 'mms:room:list',     'table',     'admin', now(), '', null, '会议室管理');
-- 按钮权限：会议记录
insert into sys_menu values(2110, '会议查询', 2104, 1, '', '', 1, 0, 'F', 0, 0, 'mms:meeting:query',  '#', 'admin', now(), '', null, '');
insert into sys_menu values(2111, '会议新增', 2104, 2, '', '', 1, 0, 'F', 0, 0, 'mms:meeting:add',    '#', 'admin', now(), '', null, '');
insert into sys_menu values(2112, '会议修改', 2104, 3, '', '', 1, 0, 'F', 0, 0, 'mms:meeting:edit',   '#', 'admin', now(), '', null, '');
insert into sys_menu values(2113, '会议删除', 2104, 4, '', '', 1, 0, 'F', 0, 0, 'mms:meeting:remove', '#', 'admin', now(), '', null, '');
-- 按钮权限：跟踪事项
insert into sys_menu values(2120, '跟踪查询', 2105, 1, '', '', 1, 0, 'F', 0, 0, 'mms:tracking:query',  '#', 'admin', now(), '', null, '');
insert into sys_menu values(2121, '跟踪新增', 2105, 2, '', '', 1, 0, 'F', 0, 0, 'mms:tracking:add',    '#', 'admin', now(), '', null, '');
insert into sys_menu values(2122, '跟踪修改', 2105, 3, '', '', 1, 0, 'F', 0, 0, 'mms:tracking:edit',   '#', 'admin', now(), '', null, '');
insert into sys_menu values(2123, '跟踪删除', 2105, 4, '', '', 1, 0, 'F', 0, 0, 'mms:tracking:remove', '#', 'admin', now(), '', null, '');
-- 按钮权限：会议室
insert into sys_menu values(2130, '会议室查询', 2106, 1, '', '', 1, 0, 'F', 0, 0, 'mms:room:query',  '#', 'admin', now(), '', null, '');
insert into sys_menu values(2131, '会议室新增', 2106, 2, '', '', 1, 0, 'F', 0, 0, 'mms:room:add',    '#', 'admin', now(), '', null, '');
insert into sys_menu values(2132, '会议室修改', 2106, 3, '', '', 1, 0, 'F', 0, 0, 'mms:room:edit',   '#', 'admin', now(), '', null, '');
insert into sys_menu values(2133, '会议室删除', 2106, 4, '', '', 1, 0, 'F', 0, 0, 'mms:room:remove', '#', 'admin', now(), '', null, '');
-- 更新菜单序列（避免手动插入与自动序列冲突）
SELECT setval('sys_menu_menu_id_seq', (SELECT MAX(menu_id) FROM sys_menu));

-- ----------------------------
-- 1. 会议室表
-- ----------------------------
CREATE TABLE IF NOT EXISTS mms_room (
    room_id       BIGSERIAL    NOT NULL PRIMARY KEY,
    campus        VARCHAR(20)  NOT NULL,                  -- 园区：1园/2园/3园
    room_number   VARCHAR(20)  NOT NULL,                  -- 会议室编号：101-109
    room_name     VARCHAR(100) DEFAULT '',                -- 会议室名称
    capacity      INT          DEFAULT 10,                -- 容纳人数
    equipment     VARCHAR(500) DEFAULT '',                -- 设备（逗号分隔）
    status        CHAR(1)      DEFAULT '0',               -- 0=正常 1=禁用
    create_by     VARCHAR(64)  DEFAULT '',
    create_time   TIMESTAMP,
    update_by     VARCHAR(64)  DEFAULT '',
    update_time   TIMESTAMP,
    remark        VARCHAR(255) DEFAULT ''
);

COMMENT ON TABLE  mms_room IS '会议室表';
COMMENT ON COLUMN mms_room.campus      IS '所属园区';
COMMENT ON COLUMN mms_room.room_number IS '房间号';
COMMENT ON COLUMN mms_room.capacity   IS '容纳人数';
COMMENT ON COLUMN mms_room.equipment  IS '设备列表（逗号分隔）';
COMMENT ON COLUMN mms_room.status     IS '状态：0正常 1禁用';

-- ----------------------------
-- 2. 会议表
-- ----------------------------
CREATE TABLE IF NOT EXISTS mms_meeting (
    meeting_id    BIGSERIAL    NOT NULL PRIMARY KEY,
    meeting_no    VARCHAR(30)  UNIQUE,                    -- 会议编号 MTG-xxxx
    title         VARCHAR(200) NOT NULL,                  -- 会议标题
    campus        VARCHAR(20)  DEFAULT '',                -- 园区（线上会议可为空）
    room_id       BIGINT,                                 -- 关联会议室
    room_number   VARCHAR(20)  DEFAULT '',                -- 会议室编号（冗余）
    category      VARCHAR(20)  DEFAULT '周边',            -- 类型：生产/周边/研发/业务
    frequency     VARCHAR(20)  DEFAULT '单次',            -- 频率：单次/日报/周报/双周报/月报
    meeting_type  CHAR(1)      DEFAULT '0',               -- 0=线下会议室 1=线上会议
    tencent_id    VARCHAR(50)  DEFAULT '',                -- 腾讯会议号
    start_time    TIMESTAMP    NOT NULL,                  -- 开始时间
    end_time      TIMESTAMP    NOT NULL,                  -- 结束时间
    host_user     VARCHAR(64)  DEFAULT '',                -- 主持人账号
    host_name     VARCHAR(100) DEFAULT '',                -- 主持人姓名
    lead_dept     VARCHAR(50)  DEFAULT '',                -- 主导部门
    description   TEXT         DEFAULT '',                -- 会议说明
    status        CHAR(1)      DEFAULT '0',               -- 0=确认 1=已取消 2=已完成
    create_by     VARCHAR(64)  DEFAULT '',
    create_time   TIMESTAMP,
    update_by     VARCHAR(64)  DEFAULT '',
    update_time   TIMESTAMP,
    remark        VARCHAR(255) DEFAULT ''
);

COMMENT ON TABLE  mms_meeting IS '会议表';
COMMENT ON COLUMN mms_meeting.category     IS '会议类型：生产/周边/研发/业务';
COMMENT ON COLUMN mms_meeting.frequency    IS '会议频率：单次/日报/周报/双周报/月报';
COMMENT ON COLUMN mms_meeting.meeting_type IS '会议类型：0线下 1线上';
COMMENT ON COLUMN mms_meeting.status       IS '状态：0确认 1已取消 2已完成';

-- ----------------------------
-- 3. 参会人员表
-- ----------------------------
CREATE TABLE IF NOT EXISTS mms_meeting_attendee (
    attendee_id    BIGSERIAL   NOT NULL PRIMARY KEY,
    meeting_id     BIGINT      NOT NULL,                  -- 关联会议
    user_id        VARCHAR(64) DEFAULT '',                -- 参会人账号
    user_name      VARCHAR(100) DEFAULT '',               -- 参会人姓名
    dept_name      VARCHAR(100) DEFAULT '',               -- 所属部门
    is_delegate    CHAR(1)     DEFAULT '0',               -- 0=本人 1=转派
    delegate_from  VARCHAR(64) DEFAULT '',                -- 原参会人
    delegate_note  VARCHAR(500) DEFAULT ''                -- 转派说明
);

COMMENT ON TABLE  mms_meeting_attendee IS '参会人员表';
COMMENT ON COLUMN mms_meeting_attendee.is_delegate IS '是否转派：0否 1是';

-- ----------------------------
-- 4. 跟踪事项表
-- ----------------------------
CREATE TABLE IF NOT EXISTS mms_tracking (
    tracking_id   BIGSERIAL    NOT NULL PRIMARY KEY,
    seq_no        INT          DEFAULT 0,                 -- 序号
    meeting_id    BIGINT,                                 -- 关联会议
    meeting_title VARCHAR(200) DEFAULT '',                -- 会议标题（冗余）
    project       VARCHAR(200) DEFAULT '',                -- 项目/议题
    dept_report   TEXT         DEFAULT '',                -- 部门汇报内容
    director_note TEXT         DEFAULT '',                -- 主任指示
    lead_person   VARCHAR(64)  DEFAULT '',                -- 责任人账号
    lead_dept     VARCHAR(50)  DEFAULT '',                -- 责任部门代码
    track_dept    VARCHAR(100) DEFAULT '',                -- 跟踪部门
    init_dept     VARCHAR(100) DEFAULT '',                -- 发起部门
    status        CHAR(1)      DEFAULT '0',               -- 0=进行中 1=已结案
    task_date     DATE,                                   -- 任务日期
    planned_date  DATE,                                   -- 计划完成日期
    create_by     VARCHAR(64)  DEFAULT '',
    create_time   TIMESTAMP,
    update_by     VARCHAR(64)  DEFAULT '',
    update_time   TIMESTAMP,
    remark        VARCHAR(255) DEFAULT ''
);

COMMENT ON TABLE  mms_tracking IS '跟踪事项表';
COMMENT ON COLUMN mms_tracking.status IS '状态：0进行中 1已结案';

-- ----------------------------
-- 5. 跟踪事项进度表
-- ----------------------------
CREATE TABLE IF NOT EXISTS mms_tracking_progress (
    progress_id   BIGSERIAL    NOT NULL PRIMARY KEY,
    tracking_id   BIGINT       NOT NULL,                  -- 关联跟踪事项
    progress_date VARCHAR(20)  DEFAULT '',                -- 进度日期（如 3/4）
    content       TEXT         DEFAULT '',                -- 进度内容
    create_by     VARCHAR(64)  DEFAULT '',
    create_time   TIMESTAMP
);

COMMENT ON TABLE  mms_tracking_progress IS '跟踪事项进度表';

-- ----------------------------
-- 初始数据：会议室
-- ----------------------------
INSERT INTO mms_room (campus, room_number, room_name, capacity, equipment, status, create_by, create_time) VALUES
('1园', '101', '1园101会议室',  8,  '投影仪,白板,视频会议系统',                 '0', 'admin', now()),
('1园', '102', '1园102会议室',  10, '投影仪,白板,电话会议',                     '0', 'admin', now()),
('1园', '103', '1园103会议室',  12, '智能大屏,白板,摄像头',                     '0', 'admin', now()),
('1园', '104', '1园104会议室',  15, '投影仪,音响系统,白板',                     '0', 'admin', now()),
('1园', '105', '1园105会议室',  20, '视频会议系统,智能大屏,白板,摄像头',         '0', 'admin', now()),
('1园', '106', '1园106会议室',   6, '白板,电话会议',                            '0', 'admin', now()),
('1园', '107', '1园107会议室',  10, '投影仪,白板',                              '0', 'admin', now()),
('1园', '108', '1园108会议室',  30, '投影仪,音响系统,视频会议系统,智能大屏',     '0', 'admin', now()),
('1园', '109', '1园109会议室',   8, '白板,摄像头',                              '0', 'admin', now()),
('2园', '101', '2园101会议室',  8,  '投影仪,白板',                              '0', 'admin', now()),
('2园', '102', '2园102会议室',  10, '视频会议系统,白板,摄像头',                  '0', 'admin', now()),
('2园', '103', '2园103会议室',  12, '投影仪,智能大屏,白板',                     '0', 'admin', now()),
('2园', '104', '2园104会议室',  15, '白板,电话会议,音响系统',                   '0', 'admin', now()),
('2园', '105', '2园105会议室',  20, '投影仪,视频会议系统,白板,摄像头',           '0', 'admin', now()),
('2园', '106', '2园106会议室',   6, '白板,投影仪',                              '0', 'admin', now()),
('2园', '107', '2园107会议室',  10, '智能大屏,白板',                            '0', 'admin', now()),
('2园', '108', '2园108会议室',  30, '音响系统,投影仪,视频会议系统',              '0', 'admin', now()),
('2园', '109', '2园109会议室',   8, '白板,摄像头,打印机',                       '0', 'admin', now()),
('3园', '101', '3园101会议室',  8,  '白板,投影仪',                              '0', 'admin', now()),
('3园', '102', '3园102会议室',  10, '视频会议系统,白板',                        '0', 'admin', now()),
('3园', '103', '3园103会议室',  12, '投影仪,智能大屏',                          '0', 'admin', now()),
('3园', '104', '3园104会议室',  15, '音响系统,视频会议系统,白板',               '0', 'admin', now()),
('3园', '105', '3园105会议室',  20, '投影仪,白板,摄像头',                       '0', 'admin', now()),
('3园', '106', '3园106会议室',   6, '白板,电话会议',                            '0', 'admin', now()),
('3园', '107', '3园107会议室',  10, '智能大屏,摄像头,白板',                     '0', 'admin', now()),
('3园', '108', '3园108会议室',  30, '音响系统,投影仪,视频会议系统,智能大屏',     '0', 'admin', now()),
('3园', '109', '3园109会议室',   8, '白板,打印机',                              '0', 'admin', now());

-- ----------------------------
-- 6. 会议室点检/交接记录表
-- ----------------------------
ALTER TABLE mms_room ADD COLUMN IF NOT EXISTS is_abnormal      CHAR(1)      DEFAULT '0';  -- 0=正常 1=异常
ALTER TABLE mms_room ADD COLUMN IF NOT EXISTS damaged_equipment VARCHAR(500) DEFAULT '';   -- 损坏设备（逗号分隔）

COMMENT ON COLUMN mms_room.is_abnormal        IS '是否异常：0正常 1异常';
COMMENT ON COLUMN mms_room.damaged_equipment  IS '损坏设备列表（逗号分隔）';

CREATE TABLE IF NOT EXISTS mms_room_inspection (
    inspection_id   BIGSERIAL    NOT NULL PRIMARY KEY,
    room_id         BIGINT       NOT NULL,               -- 关联会议室
    meeting_id      BIGINT,                              -- 关联会议（交接确认时填写，点检时为空）
    inspect_type    CHAR(1)      DEFAULT '0',            -- 0=交接确认 1=日常点检
    damaged_equip   VARCHAR(500) DEFAULT '',             -- 损坏设备（逗号分隔）
    note            TEXT         DEFAULT '',             -- 备注/说明
    result          CHAR(1)      DEFAULT '0',            -- 0=正常 1=异常
    create_by       VARCHAR(64)  DEFAULT '',
    create_time     TIMESTAMP
);

COMMENT ON TABLE  mms_room_inspection IS '会议室点检/交接记录表';
COMMENT ON COLUMN mms_room_inspection.inspect_type IS '点检类型：0交接确认 1日常点检';
COMMENT ON COLUMN mms_room_inspection.result       IS '点检结果：0正常 1异常';

-- ----------------------------
-- 7. 软删除字段（del_flag）
-- 通过 BaseMapper 软删，查询时过滤 del_flag = '0'
-- ----------------------------
ALTER TABLE mms_meeting  ADD COLUMN IF NOT EXISTS del_flag CHAR(1) DEFAULT '0';
ALTER TABLE mms_room     ADD COLUMN IF NOT EXISTS del_flag CHAR(1) DEFAULT '0';
ALTER TABLE mms_tracking ADD COLUMN IF NOT EXISTS del_flag CHAR(1) DEFAULT '0';

COMMENT ON COLUMN mms_meeting.del_flag  IS '删除标志：0正常 1已删除';
COMMENT ON COLUMN mms_room.del_flag     IS '删除标志：0正常 1已删除';
COMMENT ON COLUMN mms_tracking.del_flag IS '删除标志：0正常 1已删除';

-- ----------------------------
-- 8. 数据权限字段（dept_id）
-- 对接 RuoYi @DataScope 机制，用于行级权限过滤
-- ----------------------------
ALTER TABLE mms_meeting  ADD COLUMN IF NOT EXISTS dept_id BIGINT;
ALTER TABLE mms_tracking ADD COLUMN IF NOT EXISTS dept_id BIGINT;

COMMENT ON COLUMN mms_meeting.dept_id  IS '申请部门ID（关联 sys_dept）';
COMMENT ON COLUMN mms_tracking.dept_id IS '责任部门ID（关联 sys_dept）';

-- ----------------------------
-- 9. MMS 角色与数据权限配置
-- 执行前请先确认 sys_dept 中已建好园区/部门结构
-- ----------------------------

-- 超级管理员角色（role_id=1）：isAdmin()=true，@DataScope 自动跳过，无需配置

-- 园区管理员角色（DATA_SCOPE_CUSTOM=2，在角色管理界面手动勾选对应园区的部门）
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
VALUES ('1园管理员', 'mms_campus1_admin', 10, '2', true, true, '0', '0', 'admin', now(), '1园所有会议数据');
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
VALUES ('2园管理员', 'mms_campus2_admin', 11, '2', true, true, '0', '0', 'admin', now(), '2园所有会议数据');
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
VALUES ('3园管理员', 'mms_campus3_admin', 12, '2', true, true, '0', '0', 'admin', now(), '3园所有会议数据');

-- 部门领导角色（DATA_SCOPE_DEPT_AND_CHILD=4，自动包含本部门及所有下级）
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
VALUES ('部门领导', 'mms_dept_leader', 13, '4', true, true, '0', '0', 'admin', now(), '本部门及下级会议数据');

-- 普通员工角色（DATA_SCOPE_DEPT=3，仅本部门）
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
VALUES ('普通员工', 'mms_employee', 14, '3', true, true, '0', '0', 'admin', now(), '仅本部门会议数据');

-- 更新角色序列
SELECT setval('sys_role_role_id_seq', (SELECT MAX(role_id) FROM sys_role));

-- 为非管理员角色分配 MMS 菜单权限
-- 园区管理员：全功能（含会议室管理、跟踪事项、查询、删除）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r,
     (VALUES (2100),(2101),(2102),(2103),(2104),(2105),(2106),(2107),
             (2110),(2111),(2112),(2113)) AS m(menu_id)
WHERE r.role_key IN ('mms_campus1_admin','mms_campus2_admin','mms_campus3_admin')
ON CONFLICT DO NOTHING;

-- 部门领导：查看+预约+跟踪+修改（无删除、无会议室管理）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r,
     (VALUES (2100),(2101),(2102),(2103),(2104),(2105),
             (2110),(2111),(2112)) AS m(menu_id)
WHERE r.role_key = 'mms_dept_leader'
ON CONFLICT DO NOTHING;

-- 普通员工：仪表盘+排程+预约+会议记录（只读，无修改删除）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r,
     (VALUES (2100),(2101),(2102),(2103),(2104),(2105),
             (2110),(2111)) AS m(menu_id)
WHERE r.role_key = 'mms_employee'
ON CONFLICT DO NOTHING;

-- 提示：园区管理员配置完角色后，需在【系统管理-角色管理】中为对应角色
-- 勾选"自定义数据权限"并选择该园区下的所有部门（sys_role_dept 表）
