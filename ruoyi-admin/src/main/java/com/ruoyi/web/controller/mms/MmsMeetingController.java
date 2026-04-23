package com.ruoyi.web.controller.mms;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.MmsMeeting;
import com.ruoyi.system.domain.MmsMeetingAttendee;
import com.ruoyi.system.service.IBpmApprovalService;
import com.ruoyi.system.service.IMmsMeetingService;

/**
 * 会议管理 Controller
 */
@RestController
@RequestMapping("/mms/meeting")
public class MmsMeetingController extends BaseController
{
    @Autowired
    private IMmsMeetingService meetingService;

    @Autowired
    private IBpmApprovalService bpmApprovalService;

    @PreAuthorize("@ss.hasPermi('mms:meeting:list')")
    @GetMapping("/list")
    public TableDataInfo list(MmsMeeting meeting) {
        startPage();
        List<MmsMeeting> list = meetingService.selectMeetingList(meeting);
        return getDataTable(list);
    }

    /** 按日期获取会议排程（用于 Schedule 页面） */
    @GetMapping("/schedule")
    public AjaxResult schedule(@RequestParam String date) {
        return AjaxResult.success(meetingService.selectMeetingsByDate(date));
    }

    @PreAuthorize("@ss.hasPermi('mms:meeting:query')")
    @GetMapping("/{meetingId}")
    public AjaxResult getInfo(@PathVariable Long meetingId) {
        return AjaxResult.success(meetingService.selectMeetingById(meetingId));
    }

    @PreAuthorize("@ss.hasPermi('mms:meeting:add')")
    @Log(title = "会议管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MmsMeeting meeting) {
        meeting.setCreateBy(SecurityUtils.getUsername());
        meetingService.insertMeeting(meeting);
        // 事务提交后再调 BPM，确保回调时能查到会议记录
        if (bpmApprovalService.isEnabled()) {
            bpmApprovalService.startApproval(meeting);
        }
        AjaxResult result = AjaxResult.success();
        result.put("meetingNo", meeting.getMeetingNo());
        result.put("pendingApproval", bpmApprovalService.isEnabled());
        return result;
    }

    @PreAuthorize("@ss.hasPermi('mms:meeting:edit')")
    @Log(title = "会议管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MmsMeeting meeting) {
        meeting.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(meetingService.updateMeeting(meeting));
    }

    @PreAuthorize("@ss.hasPermi('mms:meeting:remove')")
    @Log(title = "会议管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{meetingIds}")
    public AjaxResult remove(@PathVariable Long[] meetingIds) {
        return toAjax(meetingService.deleteMeetingByIds(meetingIds));
    }

    /** 取消会议 */
    @PreAuthorize("@ss.hasPermi('mms:meeting:edit')")
    @Log(title = "会议管理", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{meetingId}")
    public AjaxResult cancel(@PathVariable Long meetingId) {
        return toAjax(meetingService.cancelMeeting(meetingId, SecurityUtils.getUsername()));
    }

    /** 完成会议 */
    @PreAuthorize("@ss.hasPermi('mms:meeting:edit')")
    @Log(title = "会议管理", businessType = BusinessType.UPDATE)
    @PutMapping("/complete/{meetingId}")
    public AjaxResult complete(@PathVariable Long meetingId) {
        return toAjax(meetingService.completeMeeting(meetingId, SecurityUtils.getUsername()));
    }

    /** 转派出席 */
    @PreAuthorize("@ss.hasPermi('mms:meeting:edit')")
    @Log(title = "会议管理", businessType = BusinessType.UPDATE)
    @PutMapping("/delegate")
    public AjaxResult delegate(@RequestBody MmsMeetingAttendee attendee) {
        return toAjax(meetingService.delegateAttendee(attendee));
    }
}
