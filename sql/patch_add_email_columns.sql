ALTER TABLE mms_meeting_attendee
    ADD COLUMN IF NOT EXISTS email        VARCHAR(200) DEFAULT '',
    ADD COLUMN IF NOT EXISTS notify_email CHAR(1)      DEFAULT '0';

COMMENT ON COLUMN mms_meeting_attendee.email        IS 'email';
COMMENT ON COLUMN mms_meeting_attendee.notify_email IS 'notify_email: 0=no 1=yes';
