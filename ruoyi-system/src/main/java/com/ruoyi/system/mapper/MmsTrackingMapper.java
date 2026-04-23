package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.MmsTracking;
import com.ruoyi.system.domain.MmsTrackingProgress;

/**
 * 跟踪事项 Mapper 接口
 *
 * <p>基础 CRUD 由 {@link BaseMapper} 提供：
 * selectById / selectList / insert / insertBatch / updateById /
 * softDeleteById / softDeleteByIds</p>
 */
public interface MmsTrackingMapper extends BaseMapper<MmsTracking>
{
    // ---------- 进度记录 ----------

    List<MmsTrackingProgress> selectProgressByTrackingId(Long trackingId);

    int insertProgress(MmsTrackingProgress progress);

    int deleteProgressByTrackingId(Long trackingId);
}
