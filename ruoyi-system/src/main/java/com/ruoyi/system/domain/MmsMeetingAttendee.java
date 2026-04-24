package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 参会人员表 mms_meeting_attendee
 */
public class MmsMeetingAttendee implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long attendeeId;

    /** 关联会议ID */
    private Long meetingId;

    /** 参会人账号 */
    private String userId;

    /** 参会人姓名 */
    private String userName;

    /** 所属部门 */
    private String deptName;

    /** 参会人邮箱 */
    private String email;

    /** 是否邮件通知：0否 1是 */
    private String notifyEmail;

    /** 是否转派：0否 1是 */
    private String isDelegate;

    /** 原参会人（转派时） */
    private String delegateFrom;

    /** 转派说明 */
    private String delegateNote;

    public Long getAttendeeId() { return attendeeId; }
    public void setAttendeeId(Long attendeeId) { this.attendeeId = attendeeId; }

    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNotifyEmail() { return notifyEmail; }
    public void setNotifyEmail(String notifyEmail) { this.notifyEmail = notifyEmail; }

    public String getIsDelegate() { return isDelegate; }
    public void setIsDelegate(String isDelegate) { this.isDelegate = isDelegate; }

    public String getDelegateFrom() { return delegateFrom; }
    public void setDelegateFrom(String delegateFrom) { this.delegateFrom = delegateFrom; }

    public String getDelegateNote() { return delegateNote; }
    public void setDelegateNote(String delegateNote) { this.delegateNote = delegateNote; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("attendeeId", getAttendeeId())
            .append("meetingId", getMeetingId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("email", getEmail())
            .append("notifyEmail", getNotifyEmail())
            .toString();
    }
}
