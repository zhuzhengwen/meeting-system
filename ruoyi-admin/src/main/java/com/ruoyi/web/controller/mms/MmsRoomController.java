package com.ruoyi.web.controller.mms;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.MmsRoom;
import com.ruoyi.system.domain.MmsRoomInspection;
import com.ruoyi.system.service.IMmsRoomService;

/**
 * 会议室管理 Controller
 */
@RestController
@RequestMapping("/mms/room")
public class MmsRoomController extends BaseController
{
    @Autowired
    private IMmsRoomService roomService;

    /** PageHelper 分页（原有方式，保持不变） */
    @PreAuthorize("@ss.hasPermi('mms:room:list')")
    @GetMapping("/list")
    public TableDataInfo list(MmsRoom room) {
        startPage();
        List<MmsRoom> list = roomService.selectRoomList(room);
        return getDataTable(list);
    }

    /**
     * MP IPage 分页（新方式）
     *
     * 请求示例：GET /mms/room/page?pageNum=1&pageSize=10&campus=1园
     * 返回示例：{ total, pages, current, size, records:[...] }
     */
    @PreAuthorize("@ss.hasPermi('mms:room:list')")
    @GetMapping("/page")
    public AjaxResult page(MmsRoom room,
                           @RequestParam(defaultValue = "1")  long pageNum,
                           @RequestParam(defaultValue = "10") long pageSize) {
        Page<MmsRoom> page = new Page<>(pageNum, pageSize);
        return AjaxResult.success(roomService.selectRoomPage(page, room));
    }

    /** 获取全部会议室（不分页，用于预约时选择） */
    @GetMapping("/all")
    public AjaxResult all(MmsRoom room) {
        return AjaxResult.success(roomService.selectRoomList(room));
    }

    @PreAuthorize("@ss.hasPermi('mms:room:query')")
    @GetMapping("/{roomId}")
    public AjaxResult getInfo(@PathVariable Long roomId) {
        return AjaxResult.success(roomService.selectRoomById(roomId));
    }

    /** 查询会议室近期使用记录 */
    @PreAuthorize("@ss.hasPermi('mms:room:query')")
    @GetMapping("/{roomId}/meetings")
    public AjaxResult getRoomMeetings(@PathVariable Long roomId) {
        return AjaxResult.success(roomService.selectRoomMeetings(roomId));
    }

    @PreAuthorize("@ss.hasPermi('mms:room:add')")
    @Log(title = "会议室管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MmsRoom room) {
        room.setCreateBy(SecurityUtils.getUsername());
        return toAjax(roomService.insertRoom(room));
    }

    @PreAuthorize("@ss.hasPermi('mms:room:edit')")
    @Log(title = "会议室管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MmsRoom room) {
        room.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(roomService.updateRoom(room));
    }

    /** 提交点检/交接记录 */
    @PreAuthorize("@ss.hasPermi('mms:room:edit')")
    @Log(title = "会议室点检", businessType = BusinessType.UPDATE)
    @PostMapping("/inspect")
    public AjaxResult inspect(@RequestBody MmsRoomInspection inspection) {
        inspection.setCreateBy(SecurityUtils.getUsername());
        return toAjax(roomService.submitInspection(inspection));
    }

    @PreAuthorize("@ss.hasPermi('mms:room:remove')")
    @Log(title = "会议室管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roomIds}")
    public AjaxResult remove(@PathVariable Long[] roomIds) {
        return toAjax(roomService.deleteRoomByIds(roomIds));
    }
}
