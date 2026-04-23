package com.ruoyi.web.controller.mms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.BpmCallbackRequest;
import com.ruoyi.system.service.IBpmApprovalService;

/**
 * BPM 审批回调接收接口
 *
 * <p>该接口不需要登录 Token，由 SecurityConfig 放行。
 * 通过请求体中的 {@code token} 字段鉴权，防止伪造调用。
 *
 * <pre>
 * BPM 调用示例：
 *   POST /mms/bpm/callback
 *   Content-Type: application/json
 *
 *   {
 *     "bizKey":            "MTG-20260423-143052",
 *     "processInstanceId": "process:meeting_approval:20260423001",
 *     "result":            "approved",          // approved | rejected
 *     "comment":          "同意，请提前准备会议材料",
 *     "approver":          "张三",
 *     "approveTime":       "2026-04-23 15:00:00",
 *     "token":             "your-callback-token"
 *   }
 *
 * 响应：
 *   成功 → {"code":200,"msg":"操作成功"}
 *   失败 → {"code":500,"msg":"错误原因"}
 * </pre>
 */
@RestController
@RequestMapping("/mms/bpm")
public class BpmCallbackController {

    private static final Logger log = LoggerFactory.getLogger(BpmCallbackController.class);

    @Autowired
    private IBpmApprovalService bpmApprovalService;

    /**
     * 接收 BPM 审批结果回调
     */
    @PostMapping("/callback")
    public AjaxResult callback(@RequestBody BpmCallbackRequest callback) {
        log.info("[BPM] 收到回调, bizKey={}, result={}, processInstanceId={}",
                callback.getBizKey(), callback.getResult(), callback.getProcessInstanceId());
        try {
            bpmApprovalService.handleCallback(callback);
            return AjaxResult.success();
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("[BPM] 处理回调异常, bizKey={}: {}", callback.getBizKey(), e.getMessage(), e);
            return AjaxResult.error("处理回调失败，请联系系统管理员");
        }
    }
}
