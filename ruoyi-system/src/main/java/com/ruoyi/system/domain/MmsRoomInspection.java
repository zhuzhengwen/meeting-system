package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 会议室点检/交接记录 mms_room_inspection
 */
public class MmsRoomInspection
{
    private static final long serialVersionUID = 1L;

    private Long inspectionId;
    /** 关联会议室 */
    private Long roomId;
    /** 关联会议（交接确认时填写，点检时为空） */
    private Long meetingId;
    /** 点检类型：0=交接确认 1=日常点检 */
    private String inspectType;
    /** 损坏设备（逗号分隔） */
    private String damagedEquip;
    /** 备注说明 */
    private String note;
    /** 点检结果：0=正常 1=异常 */
    private String result;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getInspectionId() { return inspectionId; }
    public void setInspectionId(Long inspectionId) { this.inspectionId = inspectionId; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }

    public String getInspectType() { return inspectType; }
    public void setInspectType(String inspectType) { this.inspectType = inspectType; }

    public String getDamagedEquip() { return damagedEquip; }
    public void setDamagedEquip(String damagedEquip) { this.damagedEquip = damagedEquip; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
