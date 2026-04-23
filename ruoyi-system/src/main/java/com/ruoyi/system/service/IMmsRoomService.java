package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.MmsRoom;
import com.ruoyi.system.domain.MmsRoomInspection;

/**
 * 会议室 服务接口
 */
public interface IMmsRoomService extends IService<MmsRoom>
{
    MmsRoom selectRoomById(Long roomId);

    List<MmsRoom> selectRoomList(MmsRoom room);

    /** MP IPage 分页查询 */
    IPage<MmsRoom> selectRoomPage(Page<MmsRoom> page, MmsRoom query);

    int insertRoom(MmsRoom room);

    int updateRoom(MmsRoom room);

    int deleteRoomById(Long roomId);

    int deleteRoomByIds(Long[] roomIds);

    /** 查询会议室近期使用记录 */
    List<Map<String, Object>> selectRoomMeetings(Long roomId);

    /** 提交点检/交接记录，并同步更新会议室异常状态 */
    int submitInspection(MmsRoomInspection inspection);
}
