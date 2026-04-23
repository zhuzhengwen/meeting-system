package com.ruoyi.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * BPM 对接配置
 */
@Component
@ConfigurationProperties(prefix = "bpm")
public class BpmProperties {

    /** 是否启用 BPM 审批流（false 时预约直接通过） */
    private boolean enabled = false;

    /** BPM 服务地址，如 http://bpm.example.com */
    private String url;

    /** BPM 流程定义 Key，与 BPM 侧约定 */
    private String processDefKey = "meeting_approval";

    /** 本系统对外暴露的回调地址（BPM 审批完成后调此接口） */
    private String callbackUrl;

    /**
     * 回调鉴权 Token。
     * 我方调 BPM 时传入，BPM 回调时原样带回，用于验证来源合法性。
     */
    private String callbackToken;

    /** 连接 BPM 超时（毫秒） */
    private int connectTimeout = 5000;

    /** 读取 BPM 响应超时（毫秒） */
    private int readTimeout = 10000;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getProcessDefKey() { return processDefKey; }
    public void setProcessDefKey(String processDefKey) { this.processDefKey = processDefKey; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

    public String getCallbackToken() { return callbackToken; }
    public void setCallbackToken(String callbackToken) { this.callbackToken = callbackToken; }

    public int getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(int connectTimeout) { this.connectTimeout = connectTimeout; }

    public int getReadTimeout() { return readTimeout; }
    public void setReadTimeout(int readTimeout) { this.readTimeout = readTimeout; }
}
