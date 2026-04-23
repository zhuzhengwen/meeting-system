package com.ruoyi.system.service;

import com.ruoyi.system.domain.MmsMeeting;
import com.ruoyi.system.domain.dto.BpmCallbackRequest;

/**
 * BPM 审批对接服务
 */
public interface IBpmApprovalService {

    /** 是否已启用 BPM 审批流 */
    boolean isEnabled();

    /**
     * 向 BPM 发起审批申请。
     * 调用前会议记录需已持久化（BPM 回调时通过 bizKey 查会议）。
     *
     * @param meeting 已保存的会议对象（需含 meetingNo、meetingId）
     */
    void startApproval(MmsMeeting meeting);

    /**
     * 处理 BPM 审批回调。
     * 验证 token、更新会议状态、记录 processInstanceId。
     *
     * @param callback BPM 回调参数
     * @throws IllegalArgumentException token 不合法或会议不存在时抛出
     */
    void handleCallback(BpmCallbackRequest callback);
}
