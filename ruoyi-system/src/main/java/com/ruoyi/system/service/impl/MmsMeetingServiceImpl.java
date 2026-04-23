package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private MmsMeetingMapper meetingMapper;

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
        int rows = meetingMapper.insert(meeting);
        insertAttendees(meeting);
        return rows;
    }

    @Override
    @Transactional
    public int updateMeeting(MmsMeeting meeting) {
        meetingMapper.deleteAttendeesByMeetingId(meeting.getMeetingId());
        insertAttendees(meeting);
        return meetingMapper.updateById(meeting);
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
        if (meeting.getAttendees() != null && !meeting.getAttendees().isEmpty()) {
            for (MmsMeetingAttendee attendee : meeting.getAttendees()) {
                attendee.setMeetingId(meeting.getMeetingId());
                if (attendee.getIsDelegate() == null) {
                    attendee.setIsDelegate("0");
                }
                if (attendee.getDelegateFrom() == null) {
                    attendee.setDelegateFrom("");
                }
                if (attendee.getDelegateNote() == null) {
                    attendee.setDelegateNote("");
                }
                meetingMapper.insertAttendee(attendee);
            }
        }
    }
}
