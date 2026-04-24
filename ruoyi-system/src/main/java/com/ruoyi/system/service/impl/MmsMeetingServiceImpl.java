package com.ruoyi.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.config.BpmProperties;
import com.ruoyi.system.domain.MmsMeeting;
import com.ruoyi.system.domain.MmsMeetingAttendee;
import com.ruoyi.system.mapper.MmsMeetingMapper;
import com.ruoyi.system.service.IMmsMeetingService;

/**
 * 会议 服务实现
 */
@Service
public class MmsMeetingServiceImpl implements IMmsMeetingService
{
    private static final Logger log = LoggerFactory.getLogger(MmsMeetingServiceImpl.class);

    @Autowired
    private MmsMeetingMapper meetingMapper;

    @Autowired
    private BpmProperties bpmProperties;

    /** 邮件服务（可选，未配置 mms.mail.enabled=true 时为 null） */
    @Autowired(required = false)
    private MmsEmailService emailService;

    @Override
    public MmsMeeting selectMeetingById(Long meetingId) {
        MmsMeeting meeting = meetingMapper.selectById(meetingId);
        if (meeting != null) {
            meeting.setAttendees(meetingMapper.selectAttendeesByMeetingId(meetingId));
        }
        return meeting;
    }

    @Override
    public List<MmsMeeting> selectMeetingList(MmsMeeting meeting) {
        return meetingMapper.selectByCondition(meeting);
    }

    @Override
    public List<MmsMeeting> selectMeetingsByDate(String date) {
        return meetingMapper.selectMeetingsByDate(date);
    }

    @Override
    @Transactional
    public int insertMeeting(MmsMeeting meeting) {
        if (meeting.getMeetingNo() == null || meeting.getMeetingNo().isEmpty()) {
            String dateStr = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            String suffix  = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            meeting.setMeetingNo("MTG-" + dateStr + "-" + suffix);
        }
        if (meeting.getStatus() == null || meeting.getStatus().isEmpty()) {
            meeting.setStatus(bpmProperties.isEnabled() ? "3" : "0");
        }
        int rows = meetingMapper.insert(meeting);
        insertAttendees(meeting);
        sendEmailAsync(meeting);
        return rows;
    }

    @Override
    @Transactional
    public int updateMeeting(MmsMeeting meeting) {
        meetingMapper.deleteAttendeesByMeetingId(meeting.getMeetingId());
        insertAttendees(meeting);
        int rows = meetingMapper.updateById(meeting);
        sendEmailAsync(meeting);
        return rows;
    }

    @Override
    public int deleteMeetingById(Long meetingId) {
        meetingMapper.deleteAttendeesByMeetingId(meetingId);
        return meetingMapper.softDeleteById(meetingId);
    }

    @Override
    public int deleteMeetingByIds(Long[] meetingIds) {
        for (Long id : meetingIds) {
            meetingMapper.deleteAttendeesByMeetingId(id);
        }
        return meetingMapper.softDeleteByIds(meetingIds);
    }

    @Override
    public int cancelMeeting(Long meetingId, String updateBy) {
        MmsMeeting meeting = new MmsMeeting();
        meeting.setMeetingId(meetingId);
        meeting.setStatus("1");
        meeting.setUpdateBy(updateBy);
        return meetingMapper.updateById(meeting);
    }

    @Override
    public int completeMeeting(Long meetingId, String updateBy) {
        MmsMeeting meeting = new MmsMeeting();
        meeting.setMeetingId(meetingId);
        meeting.setStatus("2");
        meeting.setUpdateBy(updateBy);
        return meetingMapper.updateById(meeting);
    }

    @Override
    public int delegateAttendee(MmsMeetingAttendee attendee) {
        return meetingMapper.updateAttendeeDelegate(attendee);
    }

    private void insertAttendees(MmsMeeting meeting) {
        if (meeting.getAttendees() == null || meeting.getAttendees().isEmpty()) return;
        for (MmsMeetingAttendee attendee : meeting.getAttendees()) {
            attendee.setMeetingId(meeting.getMeetingId());
            if (attendee.getIsDelegate()   == null) attendee.setIsDelegate("0");
            if (attendee.getDelegateFrom() == null) attendee.setDelegateFrom("");
            if (attendee.getDelegateNote() == null) attendee.setDelegateNote("");
            if (attendee.getEmail()        == null) attendee.setEmail("");
            if (attendee.getNotifyEmail()  == null) attendee.setNotifyEmail("0");
            meetingMapper.insertAttendee(attendee);
        }
    }

    /** 异步发送邮件（捕获异常，不阻断主流程） */
    private void sendEmailAsync(MmsMeeting meeting) {
        if (emailService == null || meeting.getAttendees() == null) return;
        final MmsMeeting m = meeting;
        final List<MmsMeetingAttendee> attendees = meeting.getAttendees();
        new Thread(() -> {
            try {
                emailService.sendMeetingInvitations(m, attendees);
            } catch (Exception e) {
                log.warn("邮件发送线程异常: {}", e.getMessage());
            }
        }, "mms-mail-" + meeting.getMeetingId()).start();
    }
}
