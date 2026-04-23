package com.ruoyi.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.ruoyi.system.config.BpmProperties;
import com.ruoyi.system.domain.MmsMeeting;
import com.ruoyi.system.domain.dto.BpmCallbackRequest;
import com.ruoyi.system.domain.dto.BpmStartRequest;
import com.ruoyi.system.mapper.MmsMeetingMapper;
import com.ruoyi.system.service.IBpmApprovalService;

/**
 * BPM 审批对接服务实现
 */
@Service
public class BpmApprovalServiceImpl implements IBpmApprovalService {

    private static final Logger log = LoggerFactory.getLogger(BpmApprovalServiceImpl.class);

    @Autowired
    private BpmProperties bpmProperties;

    @Autowired
    private MmsMeetingMapper meetingMapper;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(bpmProperties.getConnectTimeout());
        factory.setReadTimeout(bpmProperties.getReadTimeout());
        restTemplate = new RestTemplate(factory);
    }

    @Override
    public boolean isEnabled() {
        return bpmProperties.isEnabled();
    }

    /**
     * 向 BPM 发起审批申请。
     *
     * <p>BPM 接口约定：POST {bpm.url}/process/start
     * <br>请求体：{@link BpmStartRequest}（JSON）
     * <br>响应体示例：{"code":0,"data":{"processInstanceId":"xxx"}}
     *
     * <p>若 BPM 调用失败，仅记录日志，不抛出异常。会议保持"待审批"状态，
     * 可由管理员人工处理或通过重试机制补发。
     */
    @Override
    public void startApproval(MmsMeeting meeting) {
        if (!bpmProperties.isEnabled()) {
            return;
        }
        BpmStartRequest req = buildStartRequest(meeting);
        String url = bpmProperties.getUrl() + "/process/start";
        try {
            log.info("[BPM] 发起审批, meetingNo={}, url={}", meeting.getMeetingNo(), url);
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, req, Map.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                // BPM 响应结构：{"code":0,"data":{"processInstanceId":"xxx"}}
                Object data = resp.getBody().get("data");
                if (data instanceof Map) {
                    Object piId = ((Map<?, ?>) data).get("processInstanceId");
                    if (piId != null) {
                        MmsMeeting update = new MmsMeeting();
                        update.setMeetingId(meeting.getMeetingId());
                        update.setProcessInstanceId(piId.toString());
                        meetingMapper.updateById(update);
                        log.info("[BPM] 已记录流程实例, meetingNo={}, processInstanceId={}",
                                meeting.getMeetingNo(), piId);
                    }
                }
            } else {
                log.warn("[BPM] 发起审批响应异常, status={}, meetingNo={}",
                        resp.getStatusCode(), meeting.getMeetingNo());
            }
        } catch (Exception e) {
            log.error("[BPM] 发起审批失败, meetingNo={}: {}", meeting.getMeetingNo(), e.getMessage(), e);
        }
    }

    /**
     * 处理 BPM 回调。
     *
     * <p>审批通过 → status="0"（已确认）
     * <br>审批拒绝 → status="4"（审批拒绝）
     *
     * @throws IllegalArgumentException token 不合法或会议不存在
     */
    @Override
    public void handleCallback(BpmCallbackRequest callback) {
        // 验证回调 token
        if (!bpmProperties.getCallbackToken().equals(callback.getToken())) {
            log.warn("[BPM] 回调 Token 不合法, bizKey={}", callback.getBizKey());
            throw new IllegalArgumentException("回调 Token 不合法");
        }

        // 按 meetingNo 查会议
        MmsMeeting query = new MmsMeeting();
        query.setMeetingNo(callback.getBizKey());
        List<MmsMeeting> list = meetingMapper.selectByCondition(query);
        if (list.isEmpty()) {
            log.warn("[BPM] 回调会议不存在, bizKey={}", callback.getBizKey());
            throw new IllegalArgumentException("会议不存在: " + callback.getBizKey());
        }
        MmsMeeting meeting = list.get(0);

        // 只处理待审批状态的会议，防止重复回调
        if (!"3".equals(meeting.getStatus())) {
            log.warn("[BPM] 会议状态非待审批，忽略回调, meetingNo={}, status={}",
                    meeting.getMeetingNo(), meeting.getStatus());
            return;
        }

        String newStatus = "approved".equals(callback.getResult()) ? "0" : "4";
        MmsMeeting update = new MmsMeeting();
        update.setMeetingId(meeting.getMeetingId());
        update.setProcessInstanceId(callback.getProcessInstanceId());
        update.setStatus(newStatus);
        meetingMapper.updateById(update);

        log.info("[BPM] 审批回调处理完成, meetingNo={}, result={}, newStatus={}",
                callback.getBizKey(), callback.getResult(), newStatus);
    }

    // ── 构建发起请求 ──────────────────────────────────────────────────

    private BpmStartRequest buildStartRequest(MmsMeeting meeting) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<String, Object> formData = new HashMap<>();
        formData.put("meetingId",   meeting.getMeetingId());
        formData.put("meetingNo",   meeting.getMeetingNo());
        formData.put("title",       meeting.getTitle());
        formData.put("campus",      meeting.getCampus());
        formData.put("roomNumber",  meeting.getRoomNumber());
        formData.put("startTime",   meeting.getStartTime() != null ? sdf.format(meeting.getStartTime()) : null);
        formData.put("endTime",     meeting.getEndTime()   != null ? sdf.format(meeting.getEndTime())   : null);
        formData.put("hostName",    meeting.getHostName());
        formData.put("leadDept",    meeting.getLeadDept());
        formData.put("meetingType", meeting.getMeetingType());
        formData.put("description", meeting.getDescription());

        BpmStartRequest req = new BpmStartRequest();
        req.setProcessDefKey(bpmProperties.getProcessDefKey());
        req.setBizKey(meeting.getMeetingNo());
        req.setTitle("会议预约审批 — " + meeting.getTitle());
        req.setInitiator(meeting.getCreateBy());
        req.setInitiatorName(meeting.getHostName());
        req.setFormData(formData);
        req.setCallbackUrl(bpmProperties.getCallbackUrl());
        req.setCallbackToken(bpmProperties.getCallbackToken());
        return req;
    }
}
