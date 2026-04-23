<template>
  <div class="app-container">
    <div class="mms-page-header">
      <h2 style="margin:0;font-size:20px;color:#1f2937">预约会议</h2>
    </div>

    <!-- 会议形式切换 -->
    <div class="bk-mode-bar">
      <button :class="['bk-mode-btn', meetingType==='0' ? 'active' : '']" @click="setMode('0')">
        <i class="el-icon-office-building"></i> 预约会议室
      </button>
      <button :class="['bk-mode-btn', meetingType==='1' ? 'active' : '']" @click="setMode('1')">
        <i class="el-icon-monitor"></i> 线上会议
      </button>
    </div>

    <!-- 步骤条 -->
    <div class="bk-steps">
      <div
        :class="['bk-step', stepIndex >= 0 ? 'is-active' : '', stepIndex > 0 ? 'is-done' : '']"
      >
        <div class="bk-step-circle">{{ stepIndex > 0 ? '✓' : '1' }}</div>
        <div class="bk-step-label">{{ meetingType === '0' ? '选择园区与日期' : '选择日期与时间' }}</div>
      </div>
      <div v-if="meetingType === '0'"
        :class="['bk-step', stepIndex >= 1 ? 'is-active' : '', stepIndex > 1 ? 'is-done' : '']"
      >
        <div class="bk-step-circle">{{ stepIndex > 1 ? '✓' : '2' }}</div>
        <div class="bk-step-label">选择会议室与时段</div>
      </div>
      <div
        :class="['bk-step', stepIndex >= (meetingType === '0' ? 2 : 1) ? 'is-active' : '']"
      >
        <div class="bk-step-circle">{{ meetingType === '0' ? '3' : '2' }}</div>
        <div class="bk-step-label">填写会议信息</div>
      </div>
    </div>

    <!-- ═══ Step 1：园区 + 日期 ═══ -->
    <div v-show="step===1">
      <el-card shadow="never" style="max-width:500px;margin:0 auto">
        <el-form ref="step1Form" :model="booking" label-width="90px">
          <el-form-item v-if="meetingType==='0'" label="园区" prop="campus"
            :rules="[{required:true,message:'请选择园区',trigger:'change'}]">
            <el-select v-model="booking.campus" placeholder="请选择园区" style="width:100%">
              <el-option v-for="c in campuses" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>
          <el-form-item label="日期" prop="date"
            :rules="[{required:true,message:'请选择日期',trigger:'change'}]">
            <el-date-picker v-model="booking.date" type="date" placeholder="选择日期"
              value-format="yyyy-MM-dd" style="width:100%" :picker-options="dateOptions" />
          </el-form-item>
          <template v-if="meetingType==='1'">
            <el-form-item label="开始时间" prop="onlineStart"
              :rules="[{required:true,message:'请选择开始时间',trigger:'change'}]">
              <el-select v-model="booking.onlineStart" placeholder="选择开始时间" style="width:100%" @change="updateOnlineEnd">
                <el-option v-for="t in halfHourSlots" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
            <el-form-item label="结束时间" prop="onlineEnd"
              :rules="[{required:true,message:'请选择结束时间',trigger:'change'}]">
              <el-select v-model="booking.onlineEnd" placeholder="选择结束时间" style="width:100%">
                <el-option v-for="t in onlineEndSlots" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </template>
          <el-form-item>
            <el-button type="primary" style="width:100%" @click="goStep2">
              {{ meetingType==='0' ? '下一步：选择时段 →' : '下一步：填写信息 →' }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- ═══ Step 2：连续时间轴（仅线下） ═══ -->
    <div v-show="step===2 && meetingType==='0'">
      <el-card shadow="never" v-loading="timelineLoading">
        <!-- 工具栏 -->
        <div class="bk-toolbar">
          <div>
            <strong style="font-size:15px">{{ booking.campus }} — {{ booking.date }}</strong>
            <span style="font-size:12px;color:#9ca3af;margin-left:10px">在排程上拖拽选择时段</span>
          </div>
          <div style="display:flex;gap:8px;align-items:center">
            <span class="bk-legend-chip occupied">已占用</span>
            <span class="bk-legend-chip selecting">选中</span>
            <el-button size="mini" @click="clearSelection">清除选择</el-button>
            <el-button size="mini" @click="step=1">◀ 返回</el-button>
          </div>
        </div>

        <!-- 时间轴主体 -->
        <div class="bk-timeline-wrap">
          <!-- 表头：小时刻度 -->
          <div class="bk-header-row">
            <div class="bk-room-col"></div>
            <div class="bk-hours-ruler">
              <div v-for="h in displayHours" :key="h"
                class="bk-hour-label"
                :style="{ left: hourToPercent(h) + '%' }">
                {{ pad(h) }}:00
              </div>
              <!-- 纵向网格线（叠加在ruler上，供视觉对齐） -->
              <div v-for="h in displayHours" :key="'g'+h"
                class="bk-ruler-grid"
                :style="{ left: hourToPercent(h) + '%' }"></div>
            </div>
          </div>

          <!-- 会议室行 -->
          <div v-for="room in campusRooms" :key="room.roomId" class="bk-room-row">
            <!-- 左列：房间信息 -->
            <div class="bk-room-col">
              <div class="bk-room-number">{{ room.roomNumber }}</div>
              <div class="bk-room-meta">{{ room.capacity }}人·{{ fmtEquip(room.equipment) }}</div>
            </div>

            <!-- 右列：轨道 -->
            <div class="bk-track"
              :data-room-id="room.roomId"
              @mousedown.prevent="onMousedown(room, $event)"
              @mousemove="onMousemove(room, $event)">

              <!-- 小时网格线 -->
              <div v-for="h in displayHours" :key="'line'+h"
                class="bk-track-grid"
                :style="{ left: hourToPercent(h) + '%' }"></div>

              <!-- 当前时间红线 -->
              <div v-if="isToday" class="bk-now-line" :style="{ left: nowPercent + '%' }"></div>

              <!-- 已有会议块（灰色，显示会议名） -->
              <div v-for="m in getMeetingsForRoom(room.roomId)" :key="m.meetingId"
                class="bk-meeting-block"
                :style="calcBlockStyle(m.startTime, m.endTime)">
                <div class="bk-block-title">{{ m.title }}</div>
                <div class="bk-block-time">{{ fmtHM(m.startTime) }}–{{ fmtHM(m.endTime) }}</div>
              </div>

              <!-- 选中块（蓝色，显示时间段） -->
              <div v-if="sel.roomId === room.roomId && sel.startMins !== null && sel.endMins !== null && sel.startMins !== sel.endMins"
                class="bk-sel-block"
                :style="calcSelStyle()">
                <span class="bk-sel-time">{{ minsToHM(Math.min(sel.startMins, sel.endMins)) }}-{{ minsToHM(Math.max(sel.startMins, sel.endMins)) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 已选信息条：始终占位，仅用 visibility 控制显隐，避免布局重排闪动 -->
        <div class="bk-sel-infobar" :style="{ visibility: selValid ? 'visible' : 'hidden' }">
          <div>
            <strong>已选：</strong>
            {{ booking.campus }}{{ getSelRoomNumber() }} ·
            {{ selValid ? minsToHM(Math.min(sel.startMins, sel.endMins)) : '' }} –
            {{ selValid ? minsToHM(Math.max(sel.startMins, sel.endMins)) : '' }}
            （{{ selDuration }}）
          </div>
          <el-button type="primary" size="small" @click="goStep3">下一步：填写信息 →</el-button>
        </div>
      </el-card>
    </div>

    <!-- ═══ Step 3：填写会议信息 ═══ -->
    <div v-show="step===3 || (step===2 && meetingType==='1')">
      <el-card shadow="never" style="max-width:640px;margin:0 auto">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:18px">
          <h3 style="font-size:16px;margin:0;color:#1f2937">填写会议信息</h3>
          <el-button size="mini" @click="goBack">◀ 返回</el-button>
        </div>

        <!-- 预约摘要 -->
        <div class="bk-summary">
          <div class="bk-summary-item">
            <span class="bk-summary-label">{{ meetingType==='0' ? '会议室' : '会议形式' }}</span>
            <span class="bk-summary-val">
              {{ meetingType==='0' ? booking.campus + ' ' + getSelRoomNumber() : '线上会议' }}
            </span>
          </div>
          <div class="bk-summary-item">
            <span class="bk-summary-label">日期</span>
            <span class="bk-summary-val">{{ booking.date }}</span>
          </div>
          <div class="bk-summary-item">
            <span class="bk-summary-label">时间段</span>
            <span class="bk-summary-val">{{ summaryTimeStr }}</span>
          </div>
        </div>

        <el-form ref="infoForm" :model="meetingForm" :rules="infoRules" label-width="90px">
          <el-form-item label="会议标题" prop="title">
            <el-input v-model="meetingForm.title" placeholder="请输入会议名称" />
          </el-form-item>
          <el-row :gutter="14">
            <el-col :span="8">
              <el-form-item label="会议类型" prop="category">
                <el-select v-model="meetingForm.category" style="width:100%">
                  <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="会议频率" prop="frequency">
                <el-select v-model="meetingForm.frequency" style="width:100%">
                  <el-option v-for="f in frequencies" :key="f" :label="f" :value="f" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="主持人">
                <el-input v-model="meetingForm.hostName" placeholder="主持人姓名" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="主导部门">
            <el-input v-model="meetingForm.leadDept" placeholder="主导部门（可选）" />
          </el-form-item>
          <el-form-item label="参会人员">
            <el-input v-model="meetingForm.attendeesText" type="textarea" :rows="2"
              placeholder="参会人姓名，逗号分隔（如：张三,李四,王五）" />
          </el-form-item>
          <el-form-item label="会议说明">
            <el-input v-model="meetingForm.description" type="textarea" :rows="3"
              placeholder="会议议程、背景说明等（可选）" />
          </el-form-item>
          <el-form-item label="腾讯会议号" v-if="meetingType==='1'">
            <el-input v-model="meetingForm.tencentId" placeholder="如：123-456-789（可选）" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style="width:100%" :loading="submitting" @click="submitBooking">
              ✓ 确认预约
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
import { allRooms } from '@/api/mms/room'
import { addMeeting, getMeetingSchedule } from '@/api/mms/meeting'

const START_HOUR = 8    // 时间轴起始小时
const END_HOUR   = 21   // 时间轴结束小时（可预约到 21:00）
const TOTAL_MINS = (END_HOUR - START_HOUR) * 60  // 780 分钟
const SNAP       = 30   // 吸附到 30 分钟

export default {
  name: 'MmsBooking',
  data() {
    const today = new Date()
    return {
      step: 1,
      meetingType: '0',
      campuses: ['1园', '2园', '3园'],
      categories: ['生产', '周边', '研发', '业务'],
      frequencies: ['单次', '日报', '周报', '双周报', '月报'],
      displayHours: Array.from({ length: END_HOUR - START_HOUR + 1 }, (_, i) => START_HOUR + i),
      halfHourSlots: [],
      onlineEndSlots: [],
      campusRooms: [],
      scheduledMeetings: [],
      timelineLoading: false,
      // 拖拽状态
      dragging: false,
      dragRoomId: null,
      dragTrackLeft: 0,
      dragTrackWidth: 0,
      // 当前选择（以距 START_HOUR 的分钟数表示）
      sel: { roomId: null, startMins: null, endMins: null },
      booking: {
        campus: '',
        date: `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}-${String(today.getDate()).padStart(2,'0')}`,
        onlineStart: '',
        onlineEnd: '',
        finalStartMins: null,
        finalEndMins: null,
        selectedRoomId: null
      },
      meetingForm: { title: '', category: '周边', frequency: '单次', hostName: '', leadDept: '', attendeesText: '', description: '', tencentId: '' },
      infoRules: {
        title: [{ required: true, message: '会议标题不能为空', trigger: 'blur' }],
        category: [{ required: true, message: '请选择会议类型', trigger: 'change' }]
      },
      submitting: false,
      dateOptions: { disabledDate: d => d.getTime() < Date.now() - 86400000 }
    }
  },
  computed: {
    stepIndex() {
      if (this.meetingType === '1') return this.step === 1 ? 0 : 1
      return this.step - 1
    },
    isToday() {
      const today = new Date()
      const ds = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}-${String(today.getDate()).padStart(2,'0')}`
      return ds === this.booking.date
    },
    nowPercent() {
      const now = new Date()
      const mins = (now.getHours() - START_HOUR) * 60 + now.getMinutes()
      return Math.max(0, Math.min(100, (mins / TOTAL_MINS) * 100))
    },
    selValid() {
      return this.sel.roomId !== null &&
             this.sel.startMins !== null &&
             this.sel.endMins !== null &&
             this.sel.startMins !== this.sel.endMins
    },
    selDuration() {
      if (!this.selValid) return ''
      const mins = Math.abs(this.sel.endMins - this.sel.startMins)
      return mins < 60 ? `${mins}分钟` : `${Math.floor(mins/60)}小时${mins%60 ? mins%60+'分钟' : ''}`
    },
    summaryTimeStr() {
      if (this.meetingType === '1') {
        return `${this.booking.onlineStart} – ${this.booking.onlineEnd}`
      }
      if (this.booking.finalStartMins !== null) {
        return `${this.minsToHM(this.booking.finalStartMins)} – ${this.minsToHM(this.booking.finalEndMins)}`
      }
      return '—'
    }
  },
  mounted() {
    this.buildHalfHourSlots()
    document.addEventListener('mousemove', this.onGlobalMousemove)
    document.addEventListener('mouseup', this.onGlobalMouseup)
  },
  beforeDestroy() {
    document.removeEventListener('mousemove', this.onGlobalMousemove)
    document.removeEventListener('mouseup', this.onGlobalMouseup)
  },
  methods: {
    // ── 工具函数 ─────────────────────────────────────────────────
    pad(n) { return String(n).padStart(2, '0') },
    fmtDatetime(d) {
      return `${d.getFullYear()}-${this.pad(d.getMonth()+1)}-${this.pad(d.getDate())} ${this.pad(d.getHours())}:${this.pad(d.getMinutes())}:${this.pad(d.getSeconds())}`
    },
    minsToHM(totalMins) {
      const absH = START_HOUR + Math.floor(totalMins / 60)
      const m = totalMins % 60
      return `${this.pad(absH)}:${this.pad(m)}`
    },
    fmtHM(ts) {
      if (!ts) return ''
      const d = new Date(ts)
      return `${this.pad(d.getHours())}:${this.pad(d.getMinutes())}`
    },
    fmtEquip(str) {
      if (!str) return ''
      const arr = str.split(',').filter(Boolean)
      return arr.slice(0, 2).join('、') + (arr.length > 2 ? '…' : '')
    },
    hourToPercent(h) {
      return ((h - START_HOUR) * 60 / TOTAL_MINS) * 100
    },
    // 把 Date 对象转为相对 START_HOUR 的分钟数
    dateToRelMins(ts) {
      const d = new Date(ts)
      return (d.getHours() - START_HOUR) * 60 + d.getMinutes()
    },
    // 鼠标 clientX → 相对 START_HOUR 的分钟数（吸附到 SNAP）
    clientXToMins(clientX, trackLeft, trackWidth) {
      const ratio = Math.max(0, Math.min(1, (clientX - trackLeft) / trackWidth))
      const rawMins = ratio * TOTAL_MINS
      return Math.round(rawMins / SNAP) * SNAP
    },

    // ── 时间轴数据 ────────────────────────────────────────────────
    buildHalfHourSlots() {
      const s = []
      for (let h = START_HOUR; h <= END_HOUR; h++) {
        s.push(`${this.pad(h)}:00`)
        if (h < END_HOUR) s.push(`${this.pad(h)}:30`)
      }
      this.halfHourSlots = s
    },
    updateOnlineEnd() {
      const idx = this.halfHourSlots.indexOf(this.booking.onlineStart)
      this.onlineEndSlots = idx >= 0 ? this.halfHourSlots.slice(idx + 1) : []
      this.booking.onlineEnd = this.onlineEndSlots[1] || this.onlineEndSlots[0] || ''
    },
    getMeetingsForRoom(roomId) {
      return this.scheduledMeetings.filter(m => m.roomId === roomId)
    },

    // ── 布局计算 ──────────────────────────────────────────────────
    calcBlockStyle(startTs, endTs) {
      const startM = this.dateToRelMins(startTs)
      const endM   = this.dateToRelMins(endTs)
      const left   = Math.max(0, (startM / TOTAL_MINS) * 100)
      const width  = Math.max(0.5, ((endM - startM) / TOTAL_MINS) * 100)
      return { left: left + '%', width: width + '%' }
    },
    calcSelStyle() {
      const s = Math.min(this.sel.startMins, this.sel.endMins)
      const e = Math.max(this.sel.startMins, this.sel.endMins)
      return {
        left:  (s / TOTAL_MINS * 100) + '%',
        width: ((e - s) / TOTAL_MINS * 100) + '%'
      }
    },

    // ── 碰撞检测 ──────────────────────────────────────────────────
    /**
     * 检查 [startMins, endMins) 区间是否与该会议室已有会议重叠
     */
    isRangeOccupied(roomId, startMins, endMins) {
      return this.getMeetingsForRoom(roomId).some(m => {
        const ms = this.dateToRelMins(m.startTime)
        const me = this.dateToRelMins(m.endTime)
        return startMins < me && endMins > ms
      })
    },
    /**
     * 从 anchorMins 向 toMins 方向扩展，遇到已占用会议时截断
     */
    clampToFreeRange(roomId, anchorMins, toMins) {
      const meetings = this.getMeetingsForRoom(roomId)
        .map(m => ({ s: this.dateToRelMins(m.startTime), e: this.dateToRelMins(m.endTime) }))

      if (toMins > anchorMins) {
        // 向右拖拽：找 anchorMins 之后第一个会议的开始
        let limit = TOTAL_MINS
        meetings.forEach(m => { if (m.s >= anchorMins && m.s < limit) limit = m.s })
        return Math.min(toMins, limit)
      } else {
        // 向左拖拽：找 anchorMins 之前第一个会议的结束
        let limit = 0
        meetings.forEach(m => { if (m.e <= anchorMins && m.e > limit) limit = m.e })
        return Math.max(toMins, limit)
      }
    },

    // ── 拖拽事件 ──────────────────────────────────────────────────
    onMousedown(room, event) {
      const rect = event.currentTarget.getBoundingClientRect()
      const clickMins = this.clientXToMins(event.clientX, rect.left, rect.width)

      // 若点在已有会议块上，不启动拖拽
      if (this.isRangeOccupied(room.roomId, clickMins, clickMins + 1)) return

      this.dragging = true
      this.dragRoomId = room.roomId
      this.dragTrackLeft  = rect.left
      this.dragTrackWidth = rect.width
      this.sel = { roomId: room.roomId, startMins: clickMins, endMins: clickMins }
    },
    onMousemove(room, event) {
      // 同行内 mousemove 兜底（处理慢速移动）
      if (!this.dragging || this.dragRoomId !== room.roomId) return
      const curMins = this.clientXToMins(event.clientX, this.dragTrackLeft, this.dragTrackWidth)
      const clamped = this.clampToFreeRange(room.roomId, this.sel.startMins, curMins)
      this.sel = { ...this.sel, endMins: clamped }
    },
    onGlobalMousemove(event) {
      if (!this.dragging) return
      const curMins = this.clientXToMins(event.clientX, this.dragTrackLeft, this.dragTrackWidth)
      const clamped = this.clampToFreeRange(this.dragRoomId, this.sel.startMins, curMins)
      this.sel = { ...this.sel, endMins: clamped }
    },
    onGlobalMouseup() {
      if (!this.dragging) return
      this.dragging = false
      // 选区太小（< SNAP / 2）则视为误点击，清除
      if (Math.abs(this.sel.endMins - this.sel.startMins) < SNAP / 2) {
        this.clearSelection()
      }
    },
    clearSelection() {
      this.sel = { roomId: null, startMins: null, endMins: null }
    },

    // ── 步骤导航 ──────────────────────────────────────────────────
    setMode(type) { this.meetingType = type; this.step = 1; this.clearSelection() },
    goStep2() {
      this.$refs.step1Form.validate(valid => {
        if (!valid) return
        if (this.meetingType === '0') {
          this.step = 2
          this.loadTimeline()
        } else {
          this.step = 2
        }
      })
    },
    loadTimeline() {
      this.timelineLoading = true
      this.clearSelection()
      Promise.all([
        allRooms({ campus: this.booking.campus, status: '0' }),
        getMeetingSchedule(this.booking.date)
      ]).then(([roomRes, schedRes]) => {
        this.campusRooms = roomRes.data || []
        this.scheduledMeetings = (schedRes.data || []).filter(m => m.campus === this.booking.campus)
        this.timelineLoading = false
      }).catch(() => { this.timelineLoading = false })
    },
    goStep3() {
      const s = Math.min(this.sel.startMins, this.sel.endMins)
      const e = Math.max(this.sel.startMins, this.sel.endMins)
      this.booking.finalStartMins = s
      this.booking.finalEndMins   = e
      this.booking.selectedRoomId = this.sel.roomId
      this.step = 3
    },
    goBack() {
      this.step = this.meetingType === '0' ? 2 : 1
    },
    getSelRoomNumber() {
      const r = this.campusRooms.find(x => x.roomId === (this.sel.roomId || this.booking.selectedRoomId))
      return r ? r.roomNumber : ''
    },

    // ── 提交 ──────────────────────────────────────────────────────
    submitBooking() {
      this.$refs.infoForm.validate(valid => {
        if (!valid) return
        this.submitting = true
        let startMins, endMins
        if (this.meetingType === '1') {
          const [sh, sm] = this.booking.onlineStart.split(':').map(Number)
          const [eh, em] = this.booking.onlineEnd.split(':').map(Number)
          startMins = (sh - START_HOUR) * 60 + sm
          endMins   = (eh - START_HOUR) * 60 + em
        } else {
          startMins = this.booking.finalStartMins
          endMins   = this.booking.finalEndMins
        }
        const baseDate = new Date(this.booking.date + 'T00:00:00')
        const start = new Date(baseDate.getTime() + (START_HOUR * 60 + startMins) * 60000)
        const end   = new Date(baseDate.getTime() + (START_HOUR * 60 + endMins)   * 60000)
        const room = this.campusRooms.find(r => r.roomId === this.booking.selectedRoomId)
        const attendees = (this.meetingForm.attendeesText || '')
          .split(',').map(n => n.trim()).filter(Boolean)
          .map(name => ({ userName: name, userId: '', deptName: '', isDelegate: '0', delegateFrom: '', delegateNote: '' }))

        addMeeting({
          title: this.meetingForm.title,
          category: this.meetingForm.category,
          frequency: this.meetingForm.frequency,
          meetingType: this.meetingType,
          campus: this.meetingType === '0' ? this.booking.campus : '',
          roomId: this.meetingType === '0' ? this.booking.selectedRoomId : null,
          roomNumber: this.meetingType === '0' ? (room ? room.roomNumber : '') : '',
          startTime: this.fmtDatetime(start),
          endTime: this.fmtDatetime(end),
          hostName: this.meetingForm.hostName,
          leadDept: this.meetingForm.leadDept,
          description: this.meetingForm.description,
          tencentId: this.meetingForm.tencentId,
          status: '0',
          attendees
        }).then(() => {
          this.submitting = false
          this.$message.success('预约成功！')
          this.$router.push('/mms/meeting')
        }).catch(() => { this.submitting = false })
      })
    }
  }
}
</script>

<style scoped>
/* ── 通用 ── */
.mms-page-header { display:flex;align-items:center;justify-content:space-between;margin-bottom:16px; }

/* ── 步骤条 ── */
.bk-steps {
  display: flex;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
  margin: 20px 0 24px;
}
.bk-step {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 12px;
  background: #fff;
  border-right: 1px solid #e5e7eb;
  cursor: default;
  transition: background .2s;
}
.bk-step:last-child { border-right: none; }
.bk-step.is-active {
  background: #eef2ff;
  border-bottom: 3px solid #2563eb;
}
.bk-step-circle {
  width: 32px; height: 32px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 700;
  background: #e5e7eb; color: #9ca3af;
  margin-bottom: 8px;
}
.bk-step.is-active .bk-step-circle {
  background: #2563eb; color: #fff;
}
.bk-step.is-done {
  background: #f0fdf4;
  border-bottom: 3px solid #16a34a;
}
.bk-step.is-done .bk-step-circle {
  background: #16a34a; color: #fff;
}
.bk-step-label {
  font-size: 13px; color: #9ca3af; font-weight: 500;
}
.bk-step.is-active .bk-step-label {
  color: #2563eb;
}
.bk-step.is-done .bk-step-label {
  color: #16a34a;
}

/* ── 模式按钮 ── */
.bk-mode-bar { display:flex;gap:8px;margin-bottom:20px; }
.bk-mode-btn {
  padding:8px 20px; border-radius:6px; border:1.5px solid #d1d5db;
  background:#fff; cursor:pointer; font-size:14px; color:#374151;
  transition:all .2s; display:flex; align-items:center; gap:6px;
}
.bk-mode-btn.active { border-color:#1a56db; background:#e1eafe; color:#1a56db; font-weight:600; }

/* ── 工具栏 ── */
.bk-toolbar {
  display:flex; align-items:center; justify-content:space-between;
  margin-bottom:14px; flex-wrap:wrap; gap:8px;
}
.bk-legend-chip { font-size:12px; padding:3px 12px; border-radius:10px; font-weight:500; }
.bk-legend-chip.occupied {
  background:repeating-linear-gradient(45deg,#e5e7eb,#e5e7eb 3px,#f9fafb 3px,#f9fafb 8px);
  color:#6b7280; border:1px solid #d1d5db;
}
.bk-legend-chip.selecting { background:#dbeafe; color:#1d4ed8; border:1px solid #93c5fd; }

/* ── 时间轴容器 ── */
.bk-timeline-wrap {
  overflow-x: auto;
  min-width: 0;
  user-select: none;
}

/* ── 表头刻度行 ── */
.bk-header-row { display:flex; border-bottom:2px solid #e5e7eb; }
.bk-hours-ruler {
  flex:1; position:relative; height:28px;
}
.bk-hour-label {
  position:absolute; top:6px;
  font-size:11px; color:#9ca3af;
  transform:translateX(-50%);
  white-space:nowrap;
}
.bk-ruler-grid {
  position:absolute; top:0; bottom:0; width:1px; background:#e5e7eb;
}

/* ── 左侧房间列 ── */
.bk-room-col {
  width:140px; flex-shrink:0;
  padding:8px 10px 8px 4px;
  display:flex; flex-direction:column; justify-content:center;
  border-right:2px solid #e5e7eb;
}
.bk-room-number { font-size:15px; font-weight:700; color:#1f2937; }
.bk-room-meta   { font-size:11px; color:#9ca3af; margin-top:2px; line-height:1.4; }

/* ── 会议室行 ── */
.bk-room-row {
  display:flex; border-bottom:1px solid #f3f4f6; min-height:52px;
}
.bk-room-row:last-child { border-bottom:none; }

/* ── 轨道 ── */
.bk-track {
  flex:1; position:relative;
  background:#fafafa;
  cursor:crosshair;
  min-height:52px;
}
.bk-track:hover { background:#f5f8ff; }

/* 纵向网格线 */
.bk-track-grid {
  position:absolute; top:0; bottom:0; width:1px; background:#e5e7eb; pointer-events:none;
}

/* 当前时间红线 */
.bk-now-line {
  position:absolute; top:0; bottom:0; width:2px; background:#ef4444; z-index:10; pointer-events:none;
}

/* ── 已有会议块（灰色） ── */
.bk-meeting-block {
  position:absolute; top:5px; bottom:5px;
  background:repeating-linear-gradient(
    45deg, #e5e7eb, #e5e7eb 3px, #f3f4f6 3px, #f3f4f6 9px
  );
  border:1px solid #d1d5db; border-left:3px solid #9ca3af;
  border-radius:4px; overflow:hidden;
  padding:2px 6px; pointer-events:none; z-index:5;
  display:flex; flex-direction:column; justify-content:center;
  min-width:4px;
}
.bk-block-title {
  font-size:12px; font-weight:600; color:#6b7280;
  white-space:nowrap; overflow:hidden; text-overflow:ellipsis;
}
.bk-block-time  { font-size:10px; color:#9ca3af; }

/* ── 选中块（蓝色） ── */
.bk-sel-block {
  position:absolute; top:5px; bottom:5px;
  background:#2563eb; border:none; border-radius:4px;
  overflow:hidden; z-index:6; pointer-events:none;
  display:flex; align-items:center; justify-content:center;
  min-width:4px;
}
.bk-sel-time {
  font-size:12px; font-weight:700; color:#fff;
  white-space:nowrap; padding:0 6px;
}

/* ── 底部已选信息条 ── */
.bk-sel-infobar {
  display:flex; align-items:center; justify-content:space-between;
  background:#dcfce7; border-radius:6px; padding:10px 16px; margin-top:14px;
  font-size:14px; color:#166534; flex-wrap:wrap; gap:8px;
  border: 1px solid #86efac;
}

/* ── 预约摘要 ── */
.bk-summary {
  display:grid; grid-template-columns:repeat(3,1fr); gap:12px;
  background:#f9fafb; border-radius:8px; padding:14px; margin-bottom:20px;
  border:1px solid #e5e7eb;
}
.bk-summary-label { font-size:11px; color:#9ca3af; display:block; }
.bk-summary-val   { font-size:14px; font-weight:600; color:#1f2937; }
</style>
