package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.MmsRoom;
import com.ruoyi.system.domain.MmsRoomInspection;
import com.ruoyi.system.mapper.MmsRoomMapper;
import com.ruoyi.system.service.IMmsRoomService;

/**
 * 会议室 服务实现
 */
@Service
public class MmsRoomServiceImpl extends ServiceImpl<MmsRoomMapper, MmsRoom> implements IMmsRoomService
{
    @Autowired
    private MmsRoomMapper roomMapper;

    @Override
    public MmsRoom selectRoomById(Long roomId) {
        return roomMapper.selectById(roomId);
    }

    @Override
    public List<MmsRoom> selectRoomList(MmsRoom room) {
        return roomMapper.selectByCondition(room);
    }

    @Override
    public IPage<MmsRoom> selectRoomPage(Page<MmsRoom> page, MmsRoom query) {
        return roomMapper.selectRoomPage(page, query);
    }

    @Override
    public int insertRoom(MmsRoom room) {
        return roomMapper.insert(room);
    }

    @Override
    public int updateRoom(MmsRoom room) {
        return roomMapper.updateById(room);
    }

    @Override
    public int deleteRoomById(Long roomId) {
        return roomMapper.softDeleteById(roomId);
    }

    @Override
    public int deleteRoomByIds(Long[] roomIds) {
        return roomMapper.softDeleteByIds(roomIds);
    }

    @Override
    public List<Map<String, Object>> selectRoomMeetings(Long roomId) {
        return roomMapper.selectRoomMeetings(roomId);
    }

    @Override
    @Transactional
    public int submitInspection(MmsRoomInspection inspection) {
        roomMapper.insertInspection(inspection);
        MmsRoom room = new MmsRoom();
        room.setRoomId(inspection.getRoomId());
        room.setIsAbnormal(inspection.getResult());
        room.setDamagedEquipment("1".equals(inspection.getResult()) ? inspection.getDamagedEquip() : "");
        room.setUpdateBy(inspection.getCreateBy());
        return roomMapper.updateRoomAbnormal(room);
    }
}
