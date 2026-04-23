package com.ruoyi.web.controller.mms;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.MmsTracking;
import com.ruoyi.system.domain.MmsTrackingProgress;
import com.ruoyi.system.service.IMmsTrackingService;

/**
 * 跟踪事项管理 Controller
 */
@RestController
@RequestMapping("/mms/tracking")
public class MmsTrackingController extends BaseController
{
    @Autowired
    private IMmsTrackingService trackingService;

    @PreAuthorize("@ss.hasPermi('mms:tracking:list')")
    @GetMapping("/list")
    public TableDataInfo list(MmsTracking tracking) {
        startPage();
        List<MmsTracking> list = trackingService.selectTrackingList(tracking);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('mms:tracking:query')")
    @GetMapping("/{trackingId}")
    public AjaxResult getInfo(@PathVariable Long trackingId) {
        return AjaxResult.success(trackingService.selectTrackingById(trackingId));
    }

    @PreAuthorize("@ss.hasPermi('mms:tracking:add')")
    @Log(title = "跟踪事项管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MmsTracking tracking) {
        tracking.setCreateBy(SecurityUtils.getUsername());
        return toAjax(trackingService.insertTracking(tracking));
    }

    @PreAuthorize("@ss.hasPermi('mms:tracking:edit')")
    @Log(title = "跟踪事项管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MmsTracking tracking) {
        tracking.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(trackingService.updateTracking(tracking));
    }

    @PreAuthorize("@ss.hasPermi('mms:tracking:remove')")
    @Log(title = "跟踪事项管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{trackingIds}")
    public AjaxResult remove(@PathVariable Long[] trackingIds) {
        return toAjax(trackingService.deleteTrackingByIds(trackingIds));
    }

    /** 添加进度更新 */
    @PreAuthorize("@ss.hasPermi('mms:tracking:edit')")
    @Log(title = "跟踪事项管理", businessType = BusinessType.UPDATE)
    @PostMapping("/progress")
    public AjaxResult addProgress(@RequestBody MmsTrackingProgress progress) {
        progress.setCreateBy(SecurityUtils.getUsername());
        return toAjax(trackingService.addProgress(progress));
    }

    /** 查询进度列表 */
    @GetMapping("/progress/{trackingId}")
    public AjaxResult getProgress(@PathVariable Long trackingId) {
        return AjaxResult.success(trackingService.selectProgressByTrackingId(trackingId));
    }

    /** 结案 */
    @PreAuthorize("@ss.hasPermi('mms:tracking:edit')")
    @Log(title = "跟踪事项管理", businessType = BusinessType.UPDATE)
    @PutMapping("/close/{trackingId}")
    public AjaxResult close(@PathVariable Long trackingId) {
        MmsTracking tracking = new MmsTracking();
        tracking.setTrackingId(trackingId);
        tracking.setStatus("1");
        tracking.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(trackingService.updateTracking(tracking));
    }
}
