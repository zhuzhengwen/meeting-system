package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.system.domain.MmsMeeting;
import com.ruoyi.system.domain.MmsMeetingAttendee;

/**
 * 会议 服务接口
 */
public interface IMmsMeetingService
{
    MmsMeeting selectMeetingById(Long meetingId);

    @DataScope(deptAlias = "d")
    List<MmsMeeting> selectMeetingList(MmsMeeting meeting);

    List<MmsMeeting> selectMeetingsByDate(String date);

    int insertMeeting(MmsMeeting meeting);

    int updateMeeting(MmsMeeting meeting);

    int deleteMeetingById(Long meetingId);

    int deleteMeetingByIds(Long[] meetingIds);

    int cancelMeeting(Long meetingId, String updateBy);

    int completeMeeting(Long meetingId, String updateBy);

    int delegateAttendee(MmsMeetingAttendee attendee);
}
