package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.MmsRoom;
import com.ruoyi.system.domain.MmsRoomInspection;

/**
 * 会议室 Mapper 接口
 *
 * <p>基础 CRUD 由 {@link BaseMapper} 提供：
 * selectById / selectList / insert / updateById /
 * softDeleteById / softDeleteByIds</p>
 */
public interface MmsRoomMapper extends BaseMapper<MmsRoom>
{
    /** MP IPage 分页查询（PaginationInnerInterceptor 自动注入 LIMIT/OFFSET） */
    IPage<MmsRoom> selectRoomPage(IPage<MmsRoom> page, MmsRoom query);

    /** 更新会议室异常状态 */
    int updateRoomAbnormal(MmsRoom room);

    /** 查询会议室近期使用记录（最近20条） */
    List<Map<String, Object>> selectRoomMeetings(Long roomId);

    /** 新增点检/交接记录 */
    int insertInspection(MmsRoomInspection inspection);
}
