package com.ruoyi.system.domain;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 会议表 mms_meeting
 */
@TableName("mms_meeting")
public class MmsMeeting extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "meeting_id", type = IdType.AUTO)
    private Long meetingId;
    private String meetingNo;
    private String title;
    private String campus;
    private Long roomId;
    private String roomNumber;
    private String category;
    private String frequency;
    private String meetingType;
    private String tencentId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String hostUser;
    private String hostName;
    private String leadDept;
    private String description;
    private String status;

    /** BPM 流程实例 ID，发起审批后由 BPM 返回 */
    private String processInstanceId;

    /** 参会人员列表（非 DB 字段） */
    @TableField(exist = false)
    private List<MmsMeetingAttendee> attendees;

    /** 查询辅助：开始日期（非 DB 字段） */
    @TableField(exist = false)
    private String beginDate;

    /** 查询辅助：结束日期（非 DB 字段） */
    @TableField(exist = false)
    private String endDate;

    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }

    public String getMeetingNo() { return meetingNo; }
    public void setMeetingNo(String meetingNo) { this.meetingNo = meetingNo; }

    @NotBlank(message = "会议标题不能为空")
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getMeetingType() { return meetingType; }
    public void setMeetingType(String meetingType) { this.meetingType = meetingType; }

    public String getTencentId() { return tencentId; }
    public void setTencentId(String tencentId) { this.tencentId = tencentId; }

    @NotNull(message = "开始时间不能为空")
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    @NotNull(message = "结束时间不能为空")
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public String getHostUser() { return hostUser; }
    public void setHostUser(String hostUser) { this.hostUser = hostUser; }

    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }

    public String getLeadDept() { return leadDept; }
    public void setLeadDept(String leadDept) { this.leadDept = leadDept; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProcessInstanceId() { return processInstanceId; }
    public void setProcessInstanceId(String processInstanceId) { this.processInstanceId = processInstanceId; }

    public List<MmsMeetingAttendee> getAttendees() { return attendees; }
    public void setAttendees(List<MmsMeetingAttendee> attendees) { this.attendees = attendees; }

    public String getBeginDate() { return beginDate; }
    public void setBeginDate(String beginDate) { this.beginDate = beginDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("meetingId", getMeetingId())
            .append("title", getTitle())
            .append("campus", getCampus())
            .append("roomNumber", getRoomNumber())
            .append("category", getCategory())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("status", getStatus())
            .toString();
    }
}
