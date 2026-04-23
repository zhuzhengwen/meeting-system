package com.ruoyi.system.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 会议室表 mms_room
 */
@TableName("mms_room")
public class MmsRoom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "room_id", type = IdType.AUTO)
    private Long roomId;
    private String campus;
    private String roomNumber;
    private String roomName;
    private Integer capacity;
    private String equipment;
    private String status;
    /** 是否异常：0正常 1异常 */
    private String isAbnormal;
    /** 损坏设备列表（逗号分隔） */
    private String damagedEquipment;
    /** 当前是否有会议占用（非 DB 字段，由 XML 子查询计算） */
    @TableField(exist = false)
    private Boolean occupied;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    @NotBlank(message = "园区不能为空")
    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }

    @NotBlank(message = "房间号不能为空")
    @Size(max = 20, message = "房间号长度不超过20字符")
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIsAbnormal() { return isAbnormal; }
    public void setIsAbnormal(String isAbnormal) { this.isAbnormal = isAbnormal; }

    public String getDamagedEquipment() { return damagedEquipment; }
    public void setDamagedEquipment(String damagedEquipment) { this.damagedEquipment = damagedEquipment; }

    public Boolean getOccupied() { return occupied; }
    public void setOccupied(Boolean occupied) { this.occupied = occupied; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("roomId", getRoomId())
            .append("campus", getCampus())
            .append("roomNumber", getRoomNumber())
            .append("roomName", getRoomName())
            .append("capacity", getCapacity())
            .append("equipment", getEquipment())
            .append("status", getStatus())
            .append("isAbnormal", getIsAbnormal())
            .append("damagedEquipment", getDamagedEquipment())
            .toString();
    }
}
