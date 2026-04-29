package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.MmsTracking;
import com.ruoyi.system.domain.MmsTrackingProgress;
import com.ruoyi.system.mapper.MmsTrackingMapper;
import com.ruoyi.system.service.IMmsTrackingService;

/**
 * 跟踪事项 服务实现
 */
@Service
public class MmsTrackingServiceImpl implements IMmsTrackingService
{
    @Autowired
    private MmsTrackingMapper trackingMapper;

    @Override
    public MmsTracking selectTrackingById(Long trackingId) {
        MmsTracking tracking = trackingMapper.selectById(trackingId);
        if (tracking != null) {
            tracking.setProgressList(trackingMapper.selectProgressByTrackingId(trackingId));
        }
        return tracking;
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<MmsTracking> selectTrackingList(MmsTracking tracking) {
        List<MmsTracking> list = trackingMapper.selectByCondition(tracking);
        for (MmsTracking item : list) {
            item.setProgressList(trackingMapper.selectProgressByTrackingId(item.getTrackingId()));
        }
        return list;
    }

    @Override
    public int insertTracking(MmsTracking tracking) {
        if (tracking.getDeptId() == null) {
            try {
                Long deptId = SecurityUtils.getLoginUser().getUser().getDeptId();
                tracking.setDeptId(deptId);
            } catch (Exception ignored) {}
        }
        return trackingMapper.insert(tracking);
    }

    @Override
    public int updateTracking(MmsTracking tracking) {
        return trackingMapper.updateById(tracking);
    }

    @Override
    public int deleteTrackingById(Long trackingId) {
        trackingMapper.deleteProgressByTrackingId(trackingId);
        return trackingMapper.softDeleteById(trackingId);
    }

    @Override
    public int deleteTrackingByIds(Long[] trackingIds) {
        for (Long id : trackingIds) {
            trackingMapper.deleteProgressByTrackingId(id);
        }
        return trackingMapper.softDeleteByIds(trackingIds);
    }

    @Override
    public int addProgress(MmsTrackingProgress progress) {
        return trackingMapper.insertProgress(progress);
    }

    @Override
    public List<MmsTrackingProgress> selectProgressByTrackingId(Long trackingId) {
        return trackingMapper.selectProgressByTrackingId(trackingId);
    }
}
