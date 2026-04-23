package com.ruoyi.system.domain.dto;

import java.util.Map;

/**
 * 发往 BPM 的发起审批请求体
 *
 * <pre>
 * 我方  ──POST──►  BPM /process/start
 * </pre>
 */
public class BpmStartRequest {

    /** 流程定义 Key，与 BPM 侧约定（如 meeting_approval） */
    private String processDefKey;

    /**
     * 业务主键，使用 meetingNo。
     * BPM 回调时原样返回，用于关联会议记录。
     */
    private String bizKey;

    /** 审批任务标题，展示在 BPM 待办列表中 */
    private String title;

    /** 发起人工号/账号（对应系统 loginName） */
    private String initiator;

    /** 发起人姓名 */
    private String initiatorName;

    /**
     * 表单数据，key-value 格式，BPM 在审批页面展示用。
     * 包含 meetingId、title、campus、roomNumber、startTime、endTime、
     * hostName、leadDept、capacity、attendeeCount、description 等。
     */
    private Map<String, Object> formData;

    /**
     * 审批完成后 BPM 回调的地址。
     * 示例：http://our-server/mms/bpm/callback
     */
    private String callbackUrl;

    /**
     * 回调鉴权 Token，BPM 回调时需在请求体中原样携带。
     * 用于防止伪造回调。
     */
    private String callbackToken;

    public String getProcessDefKey() { return processDefKey; }
    public void setProcessDefKey(String processDefKey) { this.processDefKey = processDefKey; }

    public String getBizKey() { return bizKey; }
    public void setBizKey(String bizKey) { this.bizKey = bizKey; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getInitiator() { return initiator; }
    public void setInitiator(String initiator) { this.initiator = initiator; }

    public String getInitiatorName() { return initiatorName; }
    public void setInitiatorName(String initiatorName) { this.initiatorName = initiatorName; }

    public Map<String, Object> getFormData() { return formData; }
    public void setFormData(Map<String, Object> formData) { this.formData = formData; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

    public String getCallbackToken() { return callbackToken; }
    public void setCallbackToken(String callbackToken) { this.callbackToken = callbackToken; }
}
