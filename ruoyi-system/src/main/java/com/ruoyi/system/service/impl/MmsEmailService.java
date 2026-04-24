package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.MmsMeeting;
import com.ruoyi.system.domain.MmsMeetingAttendee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议邀请邮件服务
 * 仅当 mms.mail.enabled=true 时激活
 */
@Service
@ConditionalOnProperty(name = "mms.mail.enabled", havingValue = "true")
public class MmsEmailService {

    private static final Logger log = LoggerFactory.getLogger(MmsEmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${mms.mail.from-name:会议管理系统}")
    private String fromName;

    /**
     * 向 notifyEmail='1' 且有邮箱的参会人发送会议邀请
     */
    public void sendMeetingInvitations(MmsMeeting meeting, List<MmsMeetingAttendee> attendees) {
        if (attendees == null || attendees.isEmpty()) return;

        List<MmsMeetingAttendee> targets = attendees.stream()
                .filter(a -> "1".equals(a.getNotifyEmail()))
                .filter(a -> a.getEmail() != null && !a.getEmail().trim().isEmpty())
                .collect(Collectors.toList());

        if (targets.isEmpty()) return;

        String subject = buildSubject(meeting);
        String htmlBody = buildHtmlBody(meeting);

        for (MmsMeetingAttendee attendee : targets) {
            try {
                MimeMessage msg = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
                helper.setFrom(from, fromName);
                helper.setTo(attendee.getEmail().trim());
                helper.setSubject(subject);
                helper.setText(htmlBody, true);
                mailSender.send(msg);
                log.info("会议邀请邮件已发送 -> {} <{}>", attendee.getUserName(), attendee.getEmail());
            } catch (Exception e) {
                log.warn("会议邀请邮件发送失败 -> {} <{}>: {}", attendee.getUserName(), attendee.getEmail(), e.getMessage());
            }
        }
    }

    private String buildSubject(MmsMeeting meeting) {
        return String.format("【会议邀请】%s", meeting.getTitle());
    }

    private String buildHtmlBody(MmsMeeting meeting) {
        String startStr = formatDatetime(meeting.getStartTime());
        String endStr   = formatDatetime(meeting.getEndTime());
        String location = "1".equals(meeting.getMeetingType())
                ? "线上会议" + (meeting.getTencentId() != null && !meeting.getTencentId().isEmpty()
                        ? "（腾讯会议号：" + meeting.getTencentId() + "）" : "")
                : (meeting.getCampus() != null ? meeting.getCampus() : "") + " " +
                  (meeting.getRoomNumber() != null ? meeting.getRoomNumber() : "");

        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                "<style>" +
                "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI','PingFang SC',sans-serif;" +
                "     background:#f4f6fb;margin:0;padding:24px;}" +
                ".card{background:#fff;border-radius:10px;max-width:560px;margin:0 auto;" +
                "      box-shadow:0 2px 12px rgba(0,0,0,0.08);overflow:hidden;}" +
                ".header{background:linear-gradient(135deg,#2563eb,#60a5fa);padding:28px 32px;}" +
                ".header h1{color:#fff;margin:0;font-size:20px;font-weight:600;}" +
                ".header p{color:rgba(255,255,255,0.8);margin:6px 0 0;font-size:13px;}" +
                ".body{padding:28px 32px;}" +
                ".row{display:flex;margin-bottom:14px;align-items:flex-start;}" +
                ".label{width:80px;font-size:13px;color:#6b7280;flex-shrink:0;padding-top:1px;}" +
                ".value{font-size:14px;color:#111827;flex:1;line-height:1.6;}" +
                ".divider{height:1px;background:#f3f4f6;margin:20px 0;}" +
                ".footer{padding:16px 32px;background:#f9fafb;font-size:12px;color:#9ca3af;" +
                "        border-top:1px solid #f3f4f6;}" +
                "</style></head><body>" +
                "<div class='card'>" +
                "  <div class='header'>" +
                "    <h1>" + escapeHtml(meeting.getTitle()) + "</h1>" +
                "    <p>您收到一份会议邀请</p>" +
                "  </div>" +
                "  <div class='body'>" +
                "    <div class='row'><span class='label'>会议编号</span><span class='value'>" + safeStr(meeting.getMeetingNo()) + "</span></div>" +
                "    <div class='row'><span class='label'>开始时间</span><span class='value'>" + startStr + "</span></div>" +
                "    <div class='row'><span class='label'>结束时间</span><span class='value'>" + endStr + "</span></div>" +
                "    <div class='row'><span class='label'>地点</span><span class='value'>" + escapeHtml(location.trim()) + "</span></div>" +
                (meeting.getHostName() != null && !meeting.getHostName().isEmpty()
                        ? "<div class='row'><span class='label'>主持人</span><span class='value'>" + escapeHtml(meeting.getHostName()) + "</span></div>" : "") +
                (meeting.getLeadDept() != null && !meeting.getLeadDept().isEmpty()
                        ? "<div class='row'><span class='label'>主导部门</span><span class='value'>" + escapeHtml(meeting.getLeadDept()) + "</span></div>" : "") +
                (meeting.getDescription() != null && !meeting.getDescription().isEmpty()
                        ? "<div class='divider'></div><div class='row'><span class='label'>会议说明</span><span class='value'>" + escapeHtml(meeting.getDescription()) + "</span></div>" : "") +
                "  </div>" +
                "  <div class='footer'>此邮件由会议管理系统自动发送，请勿直接回复。</div>" +
                "</div></body></html>";
    }

    private String formatDatetime(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(date);
    }

    private String safeStr(String s) {
        return s == null ? "" : escapeHtml(s);
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
