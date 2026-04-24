-- 参会人邮件通知功能补丁
-- 执行时间：2026-04
-- 说明：为 mms_meeting_attendee 添加邮箱和邮件通知字段

ALTER TABLE mms_meeting_attendee
    ADD COLUMN IF NOT EXISTS email        VARCHAR(200) DEFAULT '' COMMENT '参会人邮箱',
    ADD COLUMN IF NOT EXISTS notify_email CHAR(1)      DEFAULT '0' COMMENT '是否邮件通知：0否 1是';
