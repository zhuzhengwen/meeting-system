package com.ruoyi.system.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 查询指定会议室在 [startTime, endTime) 区间内的冲突会议数。
     * excludeMeetingId 为更新场景排除自身，新增时传 0。
     */
    int countConflicts(@Param("roomId")          Long roomId,
                       @Param("startTime")       Date startTime,
                       @Param("endTime")         Date endTime,
                       @Param("excludeMeetingId") Long excludeMeetingId);

    /** 获取 PostgreSQL 事务级 advisory lock，同 roomId 的并发请求串行化 */
    void lockRoomAdvisory(@Param("roomId") Long roomId);

    // ---------- 参会人员 ----------

    List<MmsMeetingAttendee> selectAttendeesByMeetingId(Long meetingId);

    int insertAttendee(MmsMeetingAttendee attendee);

    int deleteAttendeesByMeetingId(Long meetingId);

    int updateAttendeeDelegate(MmsMeetingAttendee attendee);
}
