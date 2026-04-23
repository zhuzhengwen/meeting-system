package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.io.Serializable;

/**
 * 跟踪事项进度表 mms_tracking_progress
 */
public class MmsTrackingProgress implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 进度ID */
    private Long progressId;

    /** 关联跟踪事项ID */
    private Long trackingId;

    /** 进度日期（如 3/4） */
    private String progressDate;

    /** 进度内容 */
    private String content;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getProgressId() { return progressId; }
    public void setProgressId(Long progressId) { this.progressId = progressId; }

    public Long getTrackingId() { return trackingId; }
    public void setTrackingId(Long trackingId) { this.trackingId = trackingId; }

    public String getProgressDate() { return progressDate; }
    public void setProgressDate(String progressDate) { this.progressDate = progressDate; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("progressId", getProgressId())
            .append("trackingId", getTrackingId())
            .append("progressDate", getProgressDate())
            .append("content", getContent())
            .toString();
    }
}
