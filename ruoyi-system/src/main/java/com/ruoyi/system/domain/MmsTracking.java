package com.ruoyi.system.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 跟踪事项表 mms_tracking
 */
@TableName("mms_tracking")
public class MmsTracking extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "tracking_id", type = IdType.AUTO)
    private Long trackingId;
    /** 责任部门ID（对接 @DataScope 行级权限） */
    private Long deptId;
    private Integer seqNo;
    private Long meetingId;
    private String meetingTitle;
    private String project;
    private String deptReport;
    private String directorNote;
    private String leadPerson;
    private String leadDept;
    private String trackDept;
    private String initDept;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date taskDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date plannedDate;

    /** 进度列表（非 DB 字段） */
    @TableField(exist = false)
    private List<MmsTrackingProgress> progressList;

    public Long getTrackingId() { return trackingId; }
    public void setTrackingId(Long trackingId) { this.trackingId = trackingId; }

    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }

    public Integer getSeqNo() { return seqNo; }
    public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }

    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }

    public String getMeetingTitle() { return meetingTitle; }
    public void setMeetingTitle(String meetingTitle) { this.meetingTitle = meetingTitle; }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    public String getDeptReport() { return deptReport; }
    public void setDeptReport(String deptReport) { this.deptReport = deptReport; }

    public String getDirectorNote() { return directorNote; }
    public void setDirectorNote(String directorNote) { this.directorNote = directorNote; }

    public String getLeadPerson() { return leadPerson; }
    public void setLeadPerson(String leadPerson) { this.leadPerson = leadPerson; }

    public String getLeadDept() { return leadDept; }
    public void setLeadDept(String leadDept) { this.leadDept = leadDept; }

    public String getTrackDept() { return trackDept; }
    public void setTrackDept(String trackDept) { this.trackDept = trackDept; }

    public String getInitDept() { return initDept; }
    public void setInitDept(String initDept) { this.initDept = initDept; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getTaskDate() { return taskDate; }
    public void setTaskDate(Date taskDate) { this.taskDate = taskDate; }

    public Date getPlannedDate() { return plannedDate; }
    public void setPlannedDate(Date plannedDate) { this.plannedDate = plannedDate; }

    public List<MmsTrackingProgress> getProgressList() { return progressList; }
    public void setProgressList(List<MmsTrackingProgress> progressList) { this.progressList = progressList; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("trackingId", getTrackingId())
            .append("project", getProject())
            .append("meetingTitle", getMeetingTitle())
            .append("status", getStatus())
            .toString();
    }
}
