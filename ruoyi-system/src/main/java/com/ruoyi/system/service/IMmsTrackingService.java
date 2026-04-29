package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.system.domain.MmsTracking;
import com.ruoyi.system.domain.MmsTrackingProgress;

/**
 * 跟踪事项 服务接口
 */
public interface IMmsTrackingService
{
    MmsTracking selectTrackingById(Long trackingId);

    @DataScope(deptAlias = "d")
    List<MmsTracking> selectTrackingList(MmsTracking tracking);

    int insertTracking(MmsTracking tracking);

    int updateTracking(MmsTracking tracking);

    int deleteTrackingById(Long trackingId);

    int deleteTrackingByIds(Long[] trackingIds);

    int addProgress(MmsTrackingProgress progress);

    List<MmsTrackingProgress> selectProgressByTrackingId(Long trackingId);
}
