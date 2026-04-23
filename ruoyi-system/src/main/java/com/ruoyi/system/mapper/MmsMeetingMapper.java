package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.MmsMeeting;
import com.ruoyi.system.domain.MmsMeetingAttendee;

/**
 * 会议 Mapper 接口
 *
 * <p>基础 CRUD 由 {@link BaseMapper} 提供：
 * selectById / selectList / insert / insertBatch / updateById /
 * softDeleteById / softDeleteByIds</p>
 */
public interface MmsMeetingMapper extends BaseMapper<MmsMeeting>
{
    /** 按日期查询当天有效会议（status != '1'） */
    List<MmsMeeting> selectMeetingsByDate(String date);

    // ---------- 参会人员 ----------

    List<MmsMeetingAttendee> selectAttendeesByMeetingId(Long meetingId);

    int insertAttendee(MmsMeetingAttendee attendee);

    int deleteAttendeesByMeetingId(Long meetingId);

    int updateAttendeeDelegate(MmsMeetingAttendee attendee);
}
