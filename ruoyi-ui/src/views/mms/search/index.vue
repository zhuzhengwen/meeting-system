<template>
  <div class="app-container">

    <!-- 搜索条件 -->
    <div class="sq-search-card">
      <div class="sq-search-row">
        <div class="sq-field">
          <span class="sq-label">关键词</span>
          <el-input v-model="query.title" placeholder="会议名称/备注" size="small" clearable
            style="width:190px" @keyup.enter.native="handleQuery" />
        </div>
        <div class="sq-field">
          <span class="sq-label">园区</span>
          <el-select v-model="query.campus" placeholder="全部" clearable size="small" style="width:90px">
            <el-option label="1园" value="1园" /><el-option label="2园" value="2园" /><el-option label="3园" value="3园" />
          </el-select>
        </div>
        <div class="sq-field">
          <span class="sq-label">类型</span>
          <el-select v-model="query.category" placeholder="全部" clearable size="small" style="width:90px">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </div>
        <div class="sq-field">
          <span class="sq-label">主导部门</span>
          <el-input v-model="query.leadDept" placeholder="全部" clearable size="small" style="width:110px" />
        </div>
        <div class="sq-field">
          <span class="sq-label">参会部门</span>
          <el-input v-model="query.attendeeDept" placeholder="全部" clearable size="small" style="width:110px" />
        </div>
        <div class="sq-field">
          <span class="sq-label">开始日期</span>
          <el-date-picker v-model="query.beginDate" type="date" value-format="yyyy-MM-dd"
            size="small" style="width:138px" placeholder="开始日期" />
        </div>
        <div class="sq-field">
          <span class="sq-label">结束日期</span>
          <el-date-picker v-model="query.endDate" type="date" value-format="yyyy-MM-dd"
            size="small" style="width:138px" placeholder="结束日期" />
        </div>
        <div class="sq-field">
          <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" size="small" @click="resetQuery">重置</el-button>
        </div>
      </div>
    </div>

    <!-- 列表 -->
    <div class="mm-table" v-loading="loading">
      <div class="sq-results-header">共 <b>{{ total }}</b> 条记录</div>
      <div class="mm-thead">
        <div class="mm-th sq-cat">类型</div>
        <div class="mm-th sq-title">会议名称</div>
        <div class="mm-th sq-loc">地点</div>
        <div class="mm-th sq-date">日期</div>
        <div class="mm-th sq-time">时间段</div>
        <div class="mm-th sq-dept">主导部门</div>
        <div class="mm-th sq-att">参会人员</div>
        <div class="mm-th sq-status">状态</div>
        <div class="mm-th sq-actions">操作</div>
      </div>

      <div v-for="m in meetingList" :key="m.meetingId" class="mm-row">
        <div class="mm-td sq-cat">
          <span :class="['mm-cat', 'cat-' + m.category]">{{ m.category }}</span>
        </div>
        <div class="mm-td sq-title">
          <span class="sq-link" @click="showDetail(m)">{{ m.title }}</span>
        </div>
        <div class="mm-td sq-loc">
          <span v-if="m.meetingType === '1'" class="mm-online">线上</span>
          <span v-else>{{ m.campus }}·{{ m.roomNumber }}</span>
        </div>
        <div class="mm-td sq-date">{{ fmtDate(m.startTime) }}</div>
        <div class="mm-td sq-time">{{ fmtTime(m.startTime) }}–{{ fmtTime(m.endTime) }}</div>
        <div class="mm-td sq-dept">{{ m.leadDept || '—' }}</div>
        <div class="mm-td sq-att sq-att-text">{{ fmtAttendees(m.attendees) }}</div>
        <div class="mm-td sq-status">
          <span :class="['mm-status', 'st-' + m.status]">{{ statusLabel(m.status) }}</span>
        </div>
        <div class="mm-td sq-actions">
          <el-button size="small" @click="showDetail(m)">详情</el-button>
          <el-button size="small" type="primary" @click="openCopy(m)">复制预约</el-button>
        </div>
      </div>

      <div v-if="!meetingList.length && !loading" class="mm-empty">暂无会议记录</div>
    </div>

    <pagination v-show="total > 0" :total="total"
      :page.sync="query.pageNum" :limit.sync="query.pageSize"
      @pagination="getList" style="margin-top:12px" />

    <!-- 详情弹窗 -->
    <el-dialog :visible.sync="detailVisible" width="580px" append-to-body>
      <div slot="title" class="dd-header">
        <div class="dd-title">{{ detail.title }}</div>
        <div class="dd-tags">
          <span :class="['mm-cat', 'cat-' + detail.category]">{{ detail.category }}</span>
          <span :class="['dd-status-tag', 'status-' + detail.status]">{{ statusLabel(detail.status) }}</span>
          <span v-if="detail.frequency" class="dd-freq">{{ detail.frequency }}</span>
        </div>
      </div>
      <div class="dd-time-banner">
        <div class="dd-time-block">
          <div class="dd-time-label">开始时间</div>
          <div class="dd-time-val">{{ fmtDatetime(detail.startTime) }}</div>
        </div>
        <div class="dd-time-arrow">→</div>
        <div class="dd-time-block">
          <div class="dd-time-label">结束时间</div>
          <div class="dd-time-val">{{ fmtDatetime(detail.endTime) }}</div>
        </div>
      </div>
      <div class="dd-info-list">
        <div class="dd-info-row">
          <span class="dd-info-label"><i class="el-icon-location-outline"></i> 地点</span>
          <span class="dd-info-val">
            <template v-if="detail.meetingType === '1'">线上会议{{ detail.tencentId ? ' · ' + detail.tencentId : '' }}</template>
            <template v-else>{{ detail.campus }} {{ detail.roomNumber }}</template>
          </span>
        </div>
        <div class="dd-info-row">
          <span class="dd-info-label"><i class="el-icon-user"></i> 主持人</span>
          <span class="dd-info-val">{{ detail.hostName || '—' }}</span>
        </div>
        <div class="dd-info-row">
          <span class="dd-info-label"><i class="el-icon-office-building"></i> 主导部门</span>
          <span class="dd-info-val">{{ detail.leadDept || '—' }}</span>
        </div>
        <div class="dd-info-row" v-if="detail.description">
          <span class="dd-info-label"><i class="el-icon-document"></i> 说明</span>
          <span class="dd-info-val">{{ detail.description }}</span>
        </div>
      </div>
      <div class="dd-attendees-section">
        <div class="dd-section-title">参会人员</div>
        <div v-if="detail.attendees && detail.attendees.length" class="dd-attendees">
          <span v-for="a in detail.attendees" :key="a.attendeeId"
            :class="['dd-attendee', a.isDelegate === '1' ? 'is-delegate' : '']">
            {{ a.userName }}{{ a.isDelegate === '1' ? ' (转派)' : '' }}
          </span>
        </div>
        <div v-else class="dd-empty-small">暂无参会人员</div>
      </div>
      <div slot="footer" class="dd-footer">
        <el-button size="small" @click="detailVisible = false">关闭</el-button>
        <el-button size="small" type="primary" @click="openCopy(detail); detailVisible = false">复制预约</el-button>
      </div>
    </el-dialog>

    <!-- 复制预约弹窗 -->
    <el-dialog title="复制会议预约" :visible.sync="copyVisible" width="860px"
      append-to-body :close-on-click-modal="false">

      <!-- 步骤条 -->
      <div class="bk-steps">
        <div :class="['bk-step', copyStep === 1 ? 'is-active' : 'is-done']">
          <div class="bk-step-circle">{{ copyStep > 1 ? '✓' : '1' }}</div>
          <div class="bk-step-label">选择日期与园区</div>
        </div>
        <div :class="['bk-step', copyStep === 2 ? 'is-active' : copyStep > 2 ? 'is-done' : '']">
          <div class="bk-step-circle">2</div>
          <div class="bk-step-label">选择会议室与时段，确认预约</div>
        </div>
      </div>

      <!-- Step 1 -->
      <template v-if="copyStep === 1">
        <div class="cp-source-bar">
          <span class="cp-src-item">原会议：<b>{{ copySource.title }}</b></span>
          <span class="cp-src-item">原时间：{{ fmtDatetime(copySource.startTime) }}–{{ fmtTime(copySource.endTime) }}</span>
          <span class="cp-src-item">原场地：{{ copySource.campus }}·{{ copySource.roomNumber }}</span>
          <span class="cp-src-item">时长：<b>{{ copyDuration }}</b> 分钟</span>
        </div>
        <el-form label-width="70px" style="margin-top:22px;max-width:440px">
          <el-form-item label="新日期">
            <el-date-picker v-model="copyForm.date" type="date" value-format="yyyy-MM-dd"
              placeholder="选择日期" style="width:100%" />
          </el-form-item>
          <el-form-item label="园区">
            <el-select v-model="copyForm.campus" placeholder="选择园区" style="width:100%">
              <el-option label="1园" value="1园" />
              <el-option label="2园" value="2园" />
              <el-option label="3园" value="3园" />
            </el-select>
          </el-form-item>
        </el-form>
        <el-button type="primary" :disabled="!copyForm.date || !copyForm.campus" @click="goStep2">
          查看空闲排程 →
        </el-button>
      </template>

      <!-- Step 2 -->
      <template v-if="copyStep === 2">
        <div class="cp-tl-topbar">
          <span class="cp-tl-info">
            {{ copyForm.campus }} — {{ copyForm.date }}&nbsp;&nbsp;在时间轴上拖拽选择会议时段
          </span>
          <div style="display:flex;align-items:center;gap:12px">
            <span class="cp-leg"><span class="cp-swatch cp-swatch-occ"></span>已占用</span>
            <span class="cp-leg"><span class="cp-swatch cp-swatch-sel"></span>选中</span>
            <el-button size="mini" @click="copyStep = 1; resetSel()">◀ 重新选择</el-button>
          </div>
        </div>

        <div class="cp-timeline" v-loading="schedLoading">
          <!-- 时间轴表头 -->
          <div class="cp-head">
            <div class="cp-room-col">会议室</div>
            <div class="cp-hours">
              <div v-for="h in cpHours" :key="h" class="cp-hour">{{ String(h).padStart(2,'0') }}:00</div>
            </div>
          </div>
          <!-- 每行会议室 -->
          <div v-for="room in campusRooms" :key="room.roomId" class="cp-row">
            <div class="cp-room-col">
              <div class="cp-rnum">{{ room.roomNumber }}</div>
              <div class="cp-rinfo">{{ room.capacity }}人<template v-if="room.equipment"> · {{ room.equipment }}</template></div>
            </div>
            <div class="cp-track"
              @mousedown.prevent="startDrag($event, room)"
              @mousemove.prevent="onDragMove($event)"
              @mouseup.prevent="endDrag">
              <div v-for="h in cpHours" :key="h" class="cp-vline" :style="{ left: hourPct(h) + '%' }"></div>
              <div v-for="occ in getRoomOccupied(room)" :key="occ.meetingId"
                class="cp-occ" :style="cpBlockPct(occ)" :title="occ.title"></div>
              <div v-if="selRoom && selRoom.roomId === room.roomId && selEndMin > selStartMin"
                class="cp-sel" :style="cpRangePct(selStartMin, selEndMin)">
                <span class="cp-sel-txt">{{ minsToTime(selStartMin) }}–{{ minsToTime(selEndMin) }}</span>
              </div>
              <div v-else-if="dragRoom && dragRoom.roomId === room.roomId && dragEndMin > dragStartMin"
                class="cp-sel cp-drag-preview" :style="cpRangePct(dragStartMin, dragEndMin)"></div>
            </div>
          </div>
          <div v-if="!campusRooms.length && !schedLoading" class="mm-empty">该园区暂无可用会议室</div>
        </div>

        <!-- 标题 + 备注输入（始终显示） -->
        <el-form label-position="top" size="small" class="cp-meta-form">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="会议标题" required>
                <el-input v-model="copyForm.title" placeholder="请输入会议标题（必填）" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="备注">
                <el-input v-model="copyForm.description" placeholder="备注（可选）" clearable />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <!-- 已选信息栏（始终占位，v-show 替代 v-if，避免高度抖动） -->
        <div class="cp-sel-bar">
          <div v-if="selRoom && selEndMin > selStartMin" class="cp-sel-bar-text">
            已选：<b>{{ copyForm.campus }} · {{ selRoom.roomNumber }}</b>
            &nbsp;{{ copyForm.date }} {{ minsToTime(selStartMin) }} → {{ minsToTime(selEndMin) }}
            &nbsp;（{{ selEndMin - selStartMin }} 分钟）
          </div>
          <div v-else class="cp-sel-placeholder">← 在时间轴上拖拽选择会议室和时段</div>
          <el-button type="primary" size="small" :loading="submitting"
            :disabled="!selRoom || selEndMin <= selStartMin || !copyForm.title"
            @click="confirmCopy">确认预约</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script>
import { listMeeting, getMeeting, addMeeting, getMeetingSchedule } from '@/api/mms/meeting'
import { allRooms } from '@/api/mms/room'

const CP_START = 7
const CP_END = 18
const CP_TOTAL = (CP_END - CP_START) * 60

function todayStr() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}
function daysAgoStr(n) {
  const d = new Date(); d.setDate(d.getDate() - n)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}

export default {
  name: 'MmsSearch',
  data() {
    return {
      loading: false,
      total: 0,
      meetingList: [],
      query: {
        pageNum: 1, pageSize: 15,
        title: undefined, campus: undefined, category: undefined,
        leadDept: undefined, attendeeDept: undefined,
        beginDate: daysAgoStr(30), endDate: todayStr()
      },
      categories: ['生产', '周边', '研发', '业务'],
      // 详情
      detailVisible: false,
      detail: {},
      // 复制弹窗
      copyVisible: false,
      copyStep: 1,
      copySource: {},
      copyForm: { date: '', campus: '', title: '', description: '' },
      campusRooms: [],
      dayMeetings: [],
      schedLoading: false,
      // 拖拽
      dragRoom: null,
      dragStartMin: 0,
      dragEndMin: 0,
      trackRect: null,
      _mmMove: null,
      _mmUp: null,
      // 已选
      selRoom: null,
      selStartMin: 0,
      selEndMin: 0,
      submitting: false,
      cpHours: [7,8,9,10,11,12,13,14,15,16,17]
    }
  },
  computed: {
    copyDuration() {
      if (!this.copySource.startTime || !this.copySource.endTime) return 0
      return Math.round((new Date(this.copySource.endTime) - new Date(this.copySource.startTime)) / 60000)
    }
  },
  created() { this.getList() },
  beforeDestroy() {
    document.removeEventListener('mousemove', this._mmMove)
    document.removeEventListener('mouseup', this._mmUp)
  },
  methods: {
    getList() {
      this.loading = true
      listMeeting(this.query).then(res => {
        this.meetingList = res.rows
        this.total = res.total
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleQuery() { this.query.pageNum = 1; this.getList() },
    resetQuery() {
      this.query = { pageNum: 1, pageSize: 15, beginDate: daysAgoStr(30), endDate: todayStr() }
      this.getList()
    },
    showDetail(row) {
      getMeeting(row.meetingId).then(res => {
        this.detail = res.data
        this.detailVisible = true
      })
    },
    openCopy(meeting) {
      getMeeting(meeting.meetingId).then(res => {
        this.copySource = res.data
        this.copyForm = {
          date: this.fmtDate(meeting.startTime),
          campus: meeting.campus || '1园',
          title: res.data.title || '',
          description: res.data.description || ''
        }
        this.copyStep = 1
        this.resetSel()
        this.copyVisible = true
      })
    },
    async goStep2() {
      if (!this.copyForm.date || !this.copyForm.campus) return
      this.copyStep = 2
      this.schedLoading = true
      this.resetSel()
      try {
        const [roomsRes, schedRes] = await Promise.all([
          allRooms({ status: '0' }),
          getMeetingSchedule(this.copyForm.date)
        ])
        this.campusRooms = (roomsRes.data || [])
          .filter(r => r.campus === this.copyForm.campus && r.status === '0')
          .sort((a, b) => (a.roomNumber || '').localeCompare(b.roomNumber || ''))
        this.dayMeetings = schedRes.data || []
      } finally {
        this.schedLoading = false
      }
    },
    resetSel() {
      this.selRoom = null; this.selStartMin = 0; this.selEndMin = 0
      this.dragRoom = null; this.dragStartMin = 0; this.dragEndMin = 0
    },
    getRoomOccupied(room) {
      return this.dayMeetings.filter(m =>
        m.campus === room.campus && m.roomNumber === room.roomNumber && m.status !== '1'
      )
    },
    hourPct(h) { return ((h - CP_START) / (CP_END - CP_START)) * 100 },
    cpBlockPct(m) {
      const s = new Date(m.startTime), e = new Date(m.endTime)
      return this.cpRangePct(s.getHours() * 60 + s.getMinutes(), e.getHours() * 60 + e.getMinutes())
    },
    cpRangePct(startMin, endMin) {
      const left = ((startMin - CP_START * 60) / CP_TOTAL) * 100
      const width = ((endMin - startMin) / CP_TOTAL) * 100
      return { left: Math.max(0, left) + '%', width: Math.max(0.5, width) + '%' }
    },
    startDrag(event, room) {
      const rect = event.currentTarget.getBoundingClientRect()
      this.trackRect = rect
      this.dragRoom = room
      this.selRoom = null
      const t = this.xToMins(event.clientX, rect)
      this.dragStartMin = t
      this.dragEndMin = t
      this._mmMove = e => this.onDragMove(e)
      this._mmUp = () => this.endDrag()
      document.addEventListener('mousemove', this._mmMove)
      document.addEventListener('mouseup', this._mmUp)
    },
    onDragMove(event) {
      if (!this.dragRoom || !this.trackRect) return
      const t = this.xToMins(event.clientX, this.trackRect)
      this.dragEndMin = Math.max(this.dragStartMin + 30, t)
    },
    endDrag() {
      document.removeEventListener('mousemove', this._mmMove)
      document.removeEventListener('mouseup', this._mmUp)
      if (this.dragRoom && this.dragEndMin - this.dragStartMin >= 30) {
        this.selRoom = this.dragRoom
        this.selStartMin = this.dragStartMin
        this.selEndMin = this.dragEndMin
      }
      this.dragRoom = null
    },
    xToMins(clientX, rect) {
      const x = Math.max(0, Math.min(rect.width, clientX - rect.left))
      const raw = (x / rect.width) * CP_TOTAL + CP_START * 60
      return Math.round(raw / 30) * 30
    },
    minsToTime(mins) {
      return `${String(Math.floor(mins / 60)).padStart(2,'0')}:${String(mins % 60).padStart(2,'0')}`
    },
    hasOverlap() {
      const occ = this.getRoomOccupied(this.selRoom)
      return occ.some(m => {
        const s = new Date(m.startTime), e = new Date(m.endTime)
        const sm = s.getHours() * 60 + s.getMinutes()
        const em = e.getHours() * 60 + e.getMinutes()
        return this.selStartMin < em && this.selEndMin > sm
      })
    },
    confirmCopy() {
      if (this.hasOverlap()) {
        this.$message.warning('所选时段与已有会议冲突，请重新选择')
        return
      }
      this.submitting = true
      const d = this.copyForm.date
      addMeeting({
        title: this.copyForm.title,
        campus: this.copyForm.campus,
        roomId: this.selRoom.roomId,
        roomNumber: this.selRoom.roomNumber,
        category: this.copySource.category,
        frequency: this.copySource.frequency,
        meetingType: this.copySource.meetingType || '0',
        startTime: `${d} ${this.minsToTime(this.selStartMin)}:00`,
        endTime: `${d} ${this.minsToTime(this.selEndMin)}:00`,
        hostUser: this.copySource.hostUser,
        hostName: this.copySource.hostName,
        leadDept: this.copySource.leadDept,
        description: this.copyForm.description,
        status: '0'
      }).then(() => {
        this.msgSuccess('复制预约成功')
        this.copyVisible = false
        this.getList()
      }).finally(() => { this.submitting = false })
    },
    fmtAttendees(attendees) {
      if (!attendees || !attendees.length) return '—'
      const names = attendees.map(a => a.userName || '')
      if (names.length <= 3) return names.join('、')
      return names.slice(0, 3).join('、') + ` +${names.length - 3}人`
    },
    fmtDate(ts) {
      if (!ts) return '—'
      const d = new Date(ts)
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
    },
    fmtTime(ts) {
      if (!ts) return ''
      const d = new Date(ts)
      return `${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
    },
    fmtDatetime(ts) {
      if (!ts) return '—'
      return `${this.fmtDate(ts)} ${this.fmtTime(ts)}`
    },
    statusLabel(s) { return { '0': '待开始', '1': '已取消', '2': '已完成' }[s] || s }
  }
}
</script>

<style scoped>
/* ── 搜索卡片 ── */
.sq-search-card {
  background: #fff; border: 1px solid #e5e7eb; border-radius: 8px;
  padding: 16px 20px; margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,.04);
}
.sq-search-row { display: flex; align-items: flex-end; flex-wrap: wrap; }
.sq-search-row > .sq-field { margin-right: 14px; margin-bottom: 8px; }
.sq-search-row > .sq-field:last-child { margin-right: 0; }
.sq-field { display: flex; flex-direction: column; }
.sq-label { font-size: 12px; color: #6b7280; margin-bottom: 5px; }

/* ── 结果表格 ── */
.sq-results-header {
  font-size: 13px; color: #6b7280; padding: 10px 16px;
  border-bottom: 1px solid #f3f4f6;
}
.sq-results-header b { color: #1f2937; }

/* ── 列宽 ── */
.sq-cat     { width: 60px;  flex-shrink: 0; }
.sq-title   { flex: 1; min-width: 0; }
.sq-loc     { width: 96px;  flex-shrink: 0; }
.sq-date    { width: 96px;  flex-shrink: 0; }
.sq-time    { width: 118px; flex-shrink: 0; }
.sq-dept    { width: 84px;  flex-shrink: 0; }
.sq-att     { width: 200px; flex-shrink: 0; }
.sq-status  { width: 76px;  flex-shrink: 0; }
.sq-actions { width: 148px; flex-shrink: 0; }

/* ── 共用表格样式 ── */
.mm-table { background:#fff; border:1px solid #e5e7eb; border-radius:10px; overflow:hidden; }
.mm-thead { display:flex; align-items:center; background:#f9fafb; padding:10px 16px; border-bottom:1px solid #e5e7eb; }
.mm-th { font-size:12px; font-weight:600; color:#6b7280; }
.mm-row { display:flex; align-items:center; padding:12px 16px; border-bottom:1px solid #f3f4f6; transition:background .12s; }
.mm-row:last-child { border-bottom:none; }
.mm-row:hover { background:#f8faff; }
.mm-td { font-size:13px; color:#374151; padding-right:8px; }
.mm-empty { text-align:center; color:#9ca3af; padding:48px 0; font-size:13px; }

.sq-link { color:#2563eb; cursor:pointer; font-weight:500; }
.sq-link:hover { text-decoration:underline; }
.sq-att-text { font-size:12px; color:#6b7280; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }

.mm-online { font-size:11px; background:#e0f2fe; color:#0369a1; padding:2px 7px; border-radius:4px; }
.mm-cat { display:inline-block; font-size:11px; padding:2px 10px; border-radius:20px; font-weight:500; white-space:nowrap; }
.cat-生产 { background:#d1fae5; color:#065f46; }
.cat-周边 { background:#dbeafe; color:#1e40af; }
.cat-研发 { background:#ede9fe; color:#5b21b6; }
.cat-业务 { background:#fef3c7; color:#92400e; }

.mm-status { display:inline-block; font-size:11px; padding:3px 10px; border-radius:20px; font-weight:600; white-space:nowrap; }
.st-0 { background:#dbeafe; color:#1d4ed8; }
.st-1 { background:#f3f4f6; color:#6b7280; }
.st-2 { background:#d1fae5; color:#065f46; }

.mm-td.sq-actions { display:flex; align-items:center; }
.mm-td.sq-actions .el-button + .el-button { margin-left: 5px; }

/* ── 详情弹窗 ── */
.dd-header { display:flex; flex-direction:column; gap:6px; }
.dd-title { font-size:18px; font-weight:700; color:#1f2937; line-height:1.3; }
.dd-tags { display:flex; align-items:center; gap:6px; flex-wrap:wrap; }
.dd-status-tag { display:inline-block; font-size:11px; padding:2px 10px; border-radius:20px; font-weight:600; }
.status-0 { background:#dbeafe; color:#1d4ed8; }
.status-1 { background:#f3f4f6; color:#6b7280; }
.status-2 { background:#d1fae5; color:#065f46; }
.dd-freq { display:inline-block; font-size:11px; padding:2px 10px; border-radius:20px; background:#f3f4f6; color:#6b7280; border:1px solid #e5e7eb; }
.dd-time-banner { display:flex; align-items:center; justify-content:space-around; background:#f0f5ff; border:1px solid #c7d7fb; border-radius:8px; padding:14px 20px; margin-bottom:16px; }
.dd-time-block { text-align:center; }
.dd-time-label { font-size:11px; color:#6b7280; margin-bottom:4px; }
.dd-time-val { font-size:15px; font-weight:600; color:#1e3a8a; }
.dd-time-arrow { font-size:20px; color:#93c5fd; }
.dd-info-list { margin-bottom:16px; }
.dd-info-row { display:flex; align-items:flex-start; gap:10px; padding:8px 0; border-bottom:1px solid #f3f4f6; font-size:13px; }
.dd-info-row:last-child { border-bottom:none; }
.dd-info-label { width:80px; flex-shrink:0; color:#9ca3af; }
.dd-info-label i { margin-right:4px; }
.dd-info-val { flex:1; color:#1f2937; font-weight:500; line-height:1.5; }
.dd-attendees-section { background:#f9fafb; border-radius:8px; padding:12px 14px; margin-bottom:4px; }
.dd-section-title { font-size:12px; font-weight:600; color:#6b7280; margin-bottom:10px; }
.dd-attendees { display:flex; flex-wrap:wrap; gap:8px; }
.dd-attendee { display:inline-flex; align-items:center; font-size:13px; padding:4px 12px; border-radius:20px; background:#fff; border:1px solid #e5e7eb; color:#374151; font-weight:500; }
.dd-attendee.is-delegate { background:#eff6ff; border-color:#bfdbfe; color:#1d4ed8; }
.dd-empty-small { font-size:13px; color:#9ca3af; }
.dd-footer { display:flex; align-items:center; justify-content:space-between; }

/* ── 复制弹窗 步骤条 ── */
.bk-steps { display:flex; border:1px solid #e5e7eb; border-radius:8px; overflow:hidden; margin-bottom:20px; }
.bk-step { flex:1; display:flex; flex-direction:column; align-items:center; padding:14px 12px; background:#f9fafb; cursor:default; }
.bk-step.is-active { background:#eef2ff; border-bottom:3px solid #2563eb; }
.bk-step.is-done   { background:#f0fdf4; border-bottom:3px solid #16a34a; }
.bk-step-circle { width:26px; height:26px; border-radius:50%; background:#d1d5db; color:#fff; display:flex; align-items:center; justify-content:center; font-size:13px; font-weight:700; margin-bottom:6px; }
.bk-step.is-active .bk-step-circle { background:#2563eb; }
.bk-step.is-done   .bk-step-circle { background:#16a34a; }
.bk-step-label { font-size:13px; color:#6b7280; text-align:center; }
.bk-step.is-active .bk-step-label { color:#2563eb; font-weight:600; }
.bk-step.is-done   .bk-step-label { color:#16a34a; }

/* ── 原会议信息条 ── */
.cp-source-bar {
  display:flex; flex-wrap:wrap; gap:20px;
  background:#f0f5ff; border:1px solid #c7d7fb; border-radius:8px;
  padding:12px 18px; font-size:13px; color:#374151;
}
.cp-src-item b { color:#1e3a8a; }

/* ── 时间轴顶栏 ── */
.cp-tl-topbar { display:flex; align-items:center; justify-content:space-between; margin-bottom:10px; }
.cp-tl-info { font-size:13px; font-weight:500; color:#374151; }
.cp-leg { display:flex; align-items:center; gap:5px; font-size:12px; color:#6b7280; }
.cp-swatch { display:inline-block; width:16px; height:10px; border-radius:3px; }
.cp-swatch-occ { background:repeating-linear-gradient(45deg,#d1d5db,#d1d5db 3px,#e5e7eb 3px,#e5e7eb 7px); border:1px solid #9ca3af; }
.cp-swatch-sel { background:#2563eb; }

/* ── 时间轴 ── */
.cp-timeline { min-width:700px; overflow-x:auto; border:1px solid #e5e7eb; border-radius:8px; overflow:hidden; }
.cp-head { display:flex; background:#f9fafb; border-bottom:2px solid #e5e7eb; padding:8px 0; }
.cp-room-col { width:140px; flex-shrink:0; padding:0 10px; display:flex; align-items:center; font-size:12px; color:#6b7280; font-weight:600; }
.cp-hours { flex:1; display:flex; }
.cp-hour { flex:1; font-size:11px; color:#9ca3af; text-align:left; padding-left:4px; }

.cp-row { display:flex; align-items:stretch; border-bottom:1px solid #f3f4f6; min-height:60px; }
.cp-row:last-child { border-bottom:none; }
.cp-room-col { flex-direction:column; justify-content:center; align-items:flex-start; gap:2px; }
.cp-rnum { font-size:13px; font-weight:600; color:#374151; }
.cp-rinfo { font-size:11px; color:#9ca3af; }

.cp-track { flex:1; position:relative; background:#fafafa; cursor:crosshair; }
.cp-vline { position:absolute; top:0; bottom:0; width:1px; background:#e5e7eb; pointer-events:none; }
.cp-occ {
  position:absolute; top:5px; bottom:5px; border-radius:4px;
  background:repeating-linear-gradient(45deg,#d1d5db,#d1d5db 4px,#e5e7eb 4px,#e5e7eb 10px);
  border:1px solid #9ca3af; pointer-events:none;
}
.cp-sel {
  position:absolute; top:5px; bottom:5px; border-radius:4px;
  background:#2563eb; opacity:.85; pointer-events:none;
  display:flex; align-items:center; justify-content:center;
}
.cp-sel-txt { font-size:11px; color:#fff; font-weight:600; white-space:nowrap; }
.cp-drag-preview { opacity:.5; }

/* ── 标题/备注输入 ── */
.cp-meta-form { margin-top:14px; }

/* ── 已选信息栏（始终显示） ── */
.cp-sel-bar {
  display:flex; align-items:center; justify-content:space-between;
  background:#f0fdf4; border:1px solid #86efac; border-radius:6px;
  padding:10px 16px; margin-top:4px;
}
.cp-sel-bar-text { font-size:13px; color:#166534; }
.cp-sel-bar-text b { font-weight:700; }
.cp-sel-placeholder { font-size:13px; color:#9ca3af; font-style:italic; }
</style>
