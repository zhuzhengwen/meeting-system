package com.ruoyi.system.domain.dto;

/**
 * BPM 审批完成后回调本系统的请求体
 *
 * <pre>
 * BPM  ──POST──►  /mms/bpm/callback
 * </pre>
 */
public class BpmCallbackRequest {

    /**
     * 业务主键，对应发起时传入的 bizKey（即 meetingNo）。
     * 用于定位会议记录。
     */
    private String bizKey;

    /** BPM 流程实例 ID，用于溯源 */
    private String processInstanceId;

    /**
     * 审批结果：
     *   approved  — 审批通过
     *   rejected  — 审批拒绝
     */
    private String result;

    /** 审批意见 */
    private String comment;

    /** 最终审批人姓名 */
    private String approver;

    /** 审批完成时间，格式 yyyy-MM-dd HH:mm:ss */
    private String approveTime;

    /**
     * 鉴权 Token，必须与发起时传入的 callbackToken 一致，
     * 否则本系统拒绝处理。
     */
    private String token;

    public String getBizKey() { return bizKey; }
    public void setBizKey(String bizKey) { this.bizKey = bizKey; }

    public String getProcessInstanceId() { return processInstanceId; }
    public void setProcessInstanceId(String processInstanceId) { this.processInstanceId = processInstanceId; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }

    public String getApproveTime() { return approveTime; }
    public void setApproveTime(String approveTime) { this.approveTime = approveTime; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
