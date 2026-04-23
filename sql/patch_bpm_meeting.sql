-- ============================================================
-- BPM 审批流对接：mms_meeting 表结构变更
-- 执行一次即可，重复执行安全（IF NOT EXISTS）
-- ============================================================

-- 1. 新增 BPM 流程实例 ID 字段
ALTER TABLE mms_meeting
    ADD COLUMN IF NOT EXISTS process_instance_id VARCHAR(128);

COMMENT ON COLUMN mms_meeting.process_instance_id IS 'BPM 流程实例 ID，发起审批后写入';

-- 2. 会议状态说明（status 字段枚举扩展）
-- 原有：0=已确认  1=已取消  2=已完成
-- 新增：3=待审批  4=审批拒绝
COMMENT ON COLUMN mms_meeting.status IS
    '会议状态：0=已确认 1=已取消 2=已完成 3=待审批 4=审批拒绝';

-- 3. 为 meeting_no 添加唯一索引（BPM 回调通过 meeting_no 定位记录）
CREATE UNIQUE INDEX IF NOT EXISTS uk_mms_meeting_no ON mms_meeting(meeting_no)
    WHERE del_flag = '0';
