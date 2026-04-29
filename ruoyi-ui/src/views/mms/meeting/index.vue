<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" class="mm-search-bar">
      <el-form-item label="会议标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入会议标题" clearable size="small"
          @keyup.enter.native="handleQuery" style="width:150px" />
      </el-form-item>
      <el-form-item label="园区" prop="campus">
        <el-select v-model="queryParams.campus" placeholder="全部" clearable size="small" style="width:80px">
          <el-option v-for="c in campuses" :key="c" :label="c" :value="c" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="category">
        <el-select v-model="queryParams.category" placeholder="全部" clearable size="small" style="width:80px">
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable size="small" style="width:100px">
          <el-option label="待开始" value="0" />
          <el-option label="已取消" value="1" />
          <el-option label="已完成" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围">
        <el-date-picker v-model="dateRange" type="daterange" size="small"
          range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          value-format="yyyy-MM-dd" style="width:220px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <div class="mm-toolbar">
      <span class="mm-list-title">会议记录列表</span>
      <div class="mm-flex-row">
        <el-button size="mini" :icon="showSearch?'el-icon-arrow-up':'el-icon-search'"
          @click="showSearch=!showSearch" plain>筛选</el-button>
        <el-button type="primary" size="small" style="margin-left:8px" @click="$router.push('/mms/booking')">+ 新建会议</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="mm-table" v-loading="loading">
      <div class="mm-thead">
        <div class="mm-th th-id">会议ID</div>
        <div class="mm-th th-title">主题</div>
        <div class="mm-th th-cat">类型</div>
        <div class="mm-th th-room">会议室</div>
        <div class="mm-th th-time">时间</div>
        <div class="mm-th th-host">主持/主导</div>
        <div class="mm-th th-status">状态</div>
        <div class="mm-th th-actions">操作</div>
      </div>

      <div v-for="m in meetingList" :key="m.meetingId" class="mm-row">
        <div class="mm-td th-id mm-id">{{ meetingNo(m) }}</div>
        <div class="mm-td th-title mm-title">{{ m.title }}</div>
        <div class="mm-td th-cat">
          <span :class="['mm-cat', 'cat-' + m.category]">{{ m.category }}</span>
        </div>
        <div class="mm-td th-room">
          <span v-if="m.meetingType==='1'" class="mm-online">线上</span>
          <span v-else>{{ m.campus }}{{ m.roomNumber }}</span>
        </div>
        <div class="mm-td th-time">
          <div class="mm-date">{{ fmtDate(m.startTime) }}</div>
          <div class="mm-time-range">{{ fmtTime(m.startTime) }}-{{ fmtTime(m.endTime) }}</div>
        </div>
        <div class="mm-td th-host">
          <div class="mm-host-name">{{ m.hostName || '—' }}</div>
          <div class="mm-host-dept" v-if="m.leadDept">[{{ m.leadDept }}]</div>
        </div>
        <div class="mm-td th-status">
          <span :class="['mm-status', 'st-' + m.status]">{{ statusLabel(m.status) }}</span>
        </div>
        <div class="mm-td th-actions">
          <button class="mm-btn mm-btn-default" @click="showDetail(m)">详情</button>
          <button class="mm-btn mm-btn-primary" @click="openCopy(m)">复制</button>
          <button v-if="m.status==='0'" v-hasPermi="['mms:meeting:edit']" class="mm-btn mm-btn-danger" @click="handleCancel(m)">取消</button>
          <button v-if="m.status==='0'" v-hasPermi="['mms:meeting:edit']" class="mm-btn mm-btn-warning" @click="handleComplete(m)">完成</button>
        </div>
      </div>

      <div v-if="!meetingList.length && !loading" class="mm-empty">暂无会议记录</div>
    </div>

    <pagination v-show="total>0" :total="total"
      :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" style="margin-top:12px" />

    <!-- 详情弹窗 -->
    <el-dialog :visible.sync="detailVisible" width="580px" append-to-body>
      <div slot="title" class="dd-header">
        <div class="dd-title">{{ detailData.title }}</div>
        <div class="dd-tags">
          <span :class="['mm-cat', 'cat-' + detailData.category]">{{ detailData.category }}</span>
          <span :class="['dd-status-tag', 'status-' + detailData.status]">{{ statusLabel(detailData.status) }}</span>
          <span v-if="detailData.frequency" class="dd-freq">{{ detailData.frequency }}</span>
        </div>
      </div>

      <div class="dd-time-banner">
        <div class="dd-time-block">
          <div class="dd-time-label">开始时间</div>
          <div class="dd-time-val">{{ formatDatetime(detailData.startTime) }}</div>
        </div>
        <div class="dd-time-arrow">→</div>
        <div class="dd-time-block">
          <div class="dd-time-label">结束时间</div>
          <div class="dd-time-val">{{ formatDatetime(detailData.endTime) }}</div>
        </div>
      </div>

      <div class="dd-info-list">
        <div class="dd-info-row">
          <span class="dd-info-label"><i class="el-icon-location-outline"></i> 地点</span>
          <span class="dd-info-val">
            <template v-if="detailData.meetingType==='1'">线上会议{{ detailData.tencentId ? ' · ' + detailData.tencentId : '' }}</template>
            <template v-else>{{ detailData.campus }} {{ detailData.roomNumber }}</template>
          </span>
        </div>
        <div class="dd-info-row">
          <span class="dd-info-label"><i class="el-icon-user"></i> 主持人</span>
          <span class="dd-info-val">{{ detailData.hostName || '—' }}</span>
        </div>
        <div class="dd-info-row">
          <span class="dd-info-label"><i class="el-icon-office-building"></i> 主导部门</span>
          <span class="dd-info-val">{{ detailData.leadDept || '—' }}</span>
        </div>
        <div class="dd-info-row" v-if="detailData.description">
          <span class="dd-info-label"><i class="el-icon-document"></i> 说明</span>
          <span class="dd-info-val">{{ detailData.description }}</span>
        </div>
      </div>

      <div class="dd-attendees-section">
        <div class="dd-section-title">参会人员</div>
        <div v-if="detailData.attendees && detailData.attendees.length" class="dd-attendees">
          <span v-for="a in detailData.attendees" :key="a.attendeeId"
            :class="['dd-attendee', a.isDelegate==='1'?'is-delegate':'']">
            {{ a.userName }}{{ a.isDelegate==='1' ? ' (转派)' : '' }}
          </span>
        </div>
        <div v-else class="dd-empty-small">暂无参会人员</div>
      </div>

      <div slot="footer" class="dd-footer">
        <el-button size="small" @click="detailVisible=false">关闭</el-button>
        <div>
          <el-button v-if="detailData.status==='0'" v-hasPermi="['mms:meeting:edit']" size="small" type="warning"
            icon="el-icon-close" @click="handleCancel(detailData)">取消会议</el-button>
          <el-button v-if="detailData.status==='0'" v-hasPermi="['mms:meeting:edit']" size="small" type="success"
            icon="el-icon-check" @click="handleComplete(detailData)">标记完成</el-button>
          <el-button size="small" type="primary" @click="openCopy(detailData); detailVisible=false">复制</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 复制预约弹窗 -->
    <el-dialog title="复制会议预约" :visible.sync="copyVisible" width="860px"
      append-to-body :close-on-click-modal="false">
      <div class="bk-steps">
        <div :class="['bk-step', copyStep===1?'is-active':'is-done']">
          <div class="bk-step-circle">{{ copyStep>1?'✓':'1' }}</div>
          <div class="bk-step-label">选择日期与园区</div>
        </div>
        <div :class="['bk-step', copyStep===2?'is-active':copyStep>2?'is-done':'']">
          <div class="bk-step-circle">2</div>
          <div class="bk-step-label">选择会议室与时段，确认预约</div>
        </div>
      </div>

      <template v-if="copyStep===1">
        <div class="cp-source-bar">
          <span class="cp-src-item">原会议：<b>{{ copySource.title }}</b></span>
          <span class="cp-src-item">原时间：{{ fmtDate(copySource.startTime) }} {{ fmtTime(copySource.startTime) }}–{{ fmtTime(copySource.endTime) }}</span>
          <span class="cp-src-item">原场地：{{ copySource.campus }}·{{ copySource.roomNumber }}</span>
          <span class="cp-src-item">时长：<b>{{ copyDuration }}</b> 分钟</span>
        </div>
        <el-form label-width="70px" style="margin-top:22px;max-width:440px">
          <el-form-item label="新日期">
            <el-date-picker v-model="copyForm.date" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width:100%" />
          </el-form-item>
          <el-form-item label="园区">
            <el-select v-model="copyForm.campus" placeholder="选择园区" style="width:100%">
              <el-option label="1园" value="1园" /><el-option label="2园" value="2园" /><el-option label="3园" value="3园" />
            </el-select>
          </el-form-item>
        </el-form>
        <el-button type="primary" :disabled="!copyForm.date||!copyForm.campus" @click="goStep2">查看空闲排程 →</el-button>
      </template>

      <template v-if="copyStep===2">
        <div class="cp-tl-topbar">
          <span class="cp-tl-info">{{ copyForm.campus }} — {{ copyForm.date }}&nbsp;&nbsp;在时间轴上拖拽选择会议时段</span>
          <div class="cp-flex-12">
            <span class="cp-leg"><span class="cp-swatch cp-swatch-occ"></span>已占用</span>
            <span class="cp-leg"><span class="cp-swatch cp-swatch-sel"></span>选中</span>
            <el-button size="mini" @click="copyStep=1;resetSel()">◀ 重新选择</el-button>
          </div>
        </div>
        <div class="cp-timeline" v-loading="schedLoading">
          <div class="cp-head">
            <div class="cp-room-col">会议室</div>
            <div class="cp-hours"><div v-for="h in cpHours" :key="h" class="cp-hour">{{ String(h).padStart(2,'0') }}:00</div></div>
          </div>
          <div v-for="room in campusRooms" :key="room.roomId" class="cp-row">
            <div class="cp-room-col">
              <div class="cp-rnum">{{ room.roomNumber }}</div>
              <div class="cp-rinfo">{{ room.capacity }}人<template v-if="room.equipment"> · {{ room.equipment }}</template></div>
            </div>
            <div class="cp-track" @mousedown.prevent="startDrag($event,room)" @mousemove.prevent="onDragMove($event)" @mouseup.prevent="endDrag">
              <div v-for="h in cpHours" :key="h" class="cp-vline" :style="{left:hourPct(h)+'%'}"></div>
              <div v-for="occ in getRoomOccupied(room)" :key="occ.meetingId" class="cp-occ" :style="cpBlockPct(occ)" :title="occ.title"></div>
              <div v-if="selRoom&&selRoom.roomId===room.roomId&&selEndMin>selStartMin" class="cp-sel" :style="cpRangePct(selStartMin,selEndMin)">
                <span class="cp-sel-txt">{{ minsToTime(selStartMin) }}–{{ minsToTime(selEndMin) }}</span>
              </div>
              <div v-else-if="dragRoom&&dragRoom.roomId===room.roomId&&dragEndMin>dragStartMin" class="cp-sel cp-drag-preview" :style="cpRangePct(dragStartMin,dragEndMin)"></div>
            </div>
          </div>
          <div v-if="!campusRooms.length&&!schedLoading" class="mm-empty">该园区暂无可用会议室</div>
        </div>
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
        <div class="cp-sel-bar">
          <div v-if="selRoom&&selEndMin>selStartMin" class="cp-sel-bar-text">
            已选：<b>{{ copyForm.campus }} · {{ selRoom.roomNumber }}</b>
            &nbsp;{{ copyForm.date }} {{ minsToTime(selStartMin) }} → {{ minsToTime(selEndMin) }}
            &nbsp;（{{ selEndMin-selStartMin }} 分钟）
          </div>
          <div v-else class="cp-sel-placeholder">← 在时间轴上拖拽选择会议室和时段</div>
          <el-button type="primary" size="small" :loading="submitting"
            :disabled="!selRoom||selEndMin<=selStartMin||!copyForm.title" @click="confirmCopy">确认预约</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { listMeeting, getMeeting, cancelMeeting, completeMeeting, addMeeting, getMeetingSchedule } from '@/api/mms/meeting'
import { allRooms } from '@/api/mms/room'

const CP_START = 7
const CP_END = 18
const CP_TOTAL = (CP_END - CP_START) * 60

export default {
  name: 'MmsMeeting',
  data() {
    return {
      loading: true,
      showSearch: false,
      total: 0,
      meetingList: [],
      campuses: ['1园', '2园', '3园'],
      categories: ['生产', '周边', '研发', '业务'],
      dateRange: [],
      queryParams: { pageNum: 1, pageSize: 10, title: undefined, campus: undefined, category: undefined, status: undefined, beginDate: undefined, endDate: undefined },
      detailVisible: false,
      detailData: {},
      // 复制预约
      copyVisible: false,
      copyStep: 1,
      copySource: {},
      copyForm: { date: '', campus: '', title: '', description: '' },
      campusRooms: [],
      dayMeetings: [],
      schedLoading: false,
      dragRoom: null, dragStartMin: 0, dragEndMin: 0, trackRect: null,
      _mmMove: null, _mmUp: null,
      selRoom: null, selStartMin: 0, selEndMin: 0,
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
      if (this.dateRange && this.dateRange.length === 2) {
        this.queryParams.beginDate = this.dateRange[0]
        this.queryParams.endDate = this.dateRange[1]
      } else {
        this.queryParams.beginDate = undefined
        this.queryParams.endDate = undefined
      }
      listMeeting(this.queryParams).then(res => {
        this.meetingList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.dateRange = []; this.handleQuery() },
    showDetail(row) {
      getMeeting(row.meetingId).then(res => {
        this.detailData = res.data
        this.detailVisible = true
      })
    },
    handleCancel(row) {
      this.$confirm(`确定取消会议"${row.title}"吗？`, '提示', { type: 'warning' }).then(() => {
        return cancelMeeting(row.meetingId)
      }).then(() => {
        this.msgSuccess('已取消')
        this.detailVisible = false
        this.getList()
      })
    },
    handleComplete(row) {
      this.$confirm(`确定将"${row.title}"标记为已完成吗？`, '提示', { type: 'warning' }).then(() => {
        return completeMeeting(row.meetingId)
      }).then(() => {
        this.msgSuccess('已完成')
        this.detailVisible = false
        this.getList()
      })
    },
    meetingNo(m) {
      return m.meetingNo || ('MTG-' + String(m.meetingId).padStart(4, '0'))
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
    formatDatetime(ts) {
      if (!ts) return '—'
      return `${this.fmtDate(ts)} ${this.fmtTime(ts)}`
    },
    statusLabel(s) { return { '0': '待开始', '1': '已取消', '2': '已完成' }[s] || s },

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
      } finally { this.schedLoading = false }
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
      this.trackRect = rect; this.dragRoom = room; this.selRoom = null
      const t = this.xToMins(event.clientX, rect)
      this.dragStartMin = t; this.dragEndMin = t
      this._mmMove = e => this.onDragMove(e)
      this._mmUp = () => this.endDrag()
      document.addEventListener('mousemove', this._mmMove)
      document.addEventListener('mouseup', this._mmUp)
    },
    onDragMove(event) {
      if (!this.dragRoom || !this.trackRect) return
      this.dragEndMin = Math.max(this.dragStartMin + 30, this.xToMins(event.clientX, this.trackRect))
    },
    endDrag() {
      document.removeEventListener('mousemove', this._mmMove)
      document.removeEventListener('mouseup', this._mmUp)
      if (this.dragRoom && this.dragEndMin - this.dragStartMin >= 30) {
        this.selRoom = this.dragRoom; this.selStartMin = this.dragStartMin; this.selEndMin = this.dragEndMin
      }
      this.dragRoom = null
    },
    xToMins(clientX, rect) {
      const x = Math.max(0, Math.min(rect.width, clientX - rect.left))
      return Math.round(((x / rect.width) * CP_TOTAL + CP_START * 60) / 30) * 30
    },
    minsToTime(mins) {
      return `${String(Math.floor(mins / 60)).padStart(2,'0')}:${String(mins % 60).padStart(2,'0')}`
    },
    hasOverlap() {
      const occ = this.getRoomOccupied(this.selRoom)
      return occ.some(m => {
        const s = new Date(m.startTime), e = new Date(m.endTime)
        const sm = s.getHours() * 60 + s.getMinutes(), em = e.getHours() * 60 + e.getMinutes()
        return this.selStartMin < em && this.selEndMin > sm
      })
    },
    confirmCopy() {
      if (this.hasOverlap()) { this.$message.warning('所选时段与已有会议冲突，请重新选择'); return }
      this.submitting = true
      const d = this.copyForm.date
      addMeeting({
        title: this.copyForm.title, campus: this.copyForm.campus,
        roomId: this.selRoom.roomId, roomNumber: this.selRoom.roomNumber,
        category: this.copySource.category, frequency: this.copySource.frequency,
        meetingType: this.copySource.meetingType || '0',
        startTime: `${d} ${this.minsToTime(this.selStartMin)}:00`,
        endTime: `${d} ${this.minsToTime(this.selEndMin)}:00`,
        hostUser: this.copySource.hostUser, hostName: this.copySource.hostName,
        leadDept: this.copySource.leadDept, description: this.copyForm.description, status: '0'
      }).then(() => {
        this.msgSuccess('复制预约成功')
        this.copyVisible = false
        this.getList()
      }).finally(() => { this.submitting = false })
    }
  }
}
</script>

<style scoped>
/* ── 搜索栏 ── */
.mm-search-bar {
  background: #f9fafb; border: 1px solid #e5e7eb;
  border-radius: 8px; padding: 14px 16px 4px; margin-bottom: 14px;
}

/* ── 工具栏 ── */
.mm-toolbar {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px;
}
.mm-list-title { font-size: 15px; font-weight: 600; color: #1f2937; }

/* ── 列宽定义 ── */
.th-id      { width: 110px; flex-shrink: 0; }
.th-title   { flex: 1; min-width: 0; }
.th-cat     { width: 76px; flex-shrink: 0; }
.th-room    { width: 88px; flex-shrink: 0; }
.th-time    { width: 138px; flex-shrink: 0; }
.th-host    { width: 116px; flex-shrink: 0; }
.th-status  { width: 80px; flex-shrink: 0; }
.th-actions { width: 248px; flex-shrink: 0; }

/* ── 表格容器 ── */
.mm-table {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
}

/* ── 表头 ── */
.mm-thead {
  display: flex; align-items: center;
  background: #f9fafb;
  padding: 10px 20px;
  border-bottom: 1px solid #e5e7eb;
}
.mm-th { font-size: 12px; font-weight: 600; color: #6b7280; }

/* ── 数据行 ── */
.mm-row {
  display: flex; align-items: center;
  padding: 13px 20px;
  border-bottom: 1px solid #f3f4f6;
  transition: background .12s;
}
.mm-row:last-child { border-bottom: none; }
.mm-row:hover { background: #f8faff; }

.mm-td { font-size: 13px; color: #374151; padding-right: 8px; }
.mm-id { font-size: 12px; color: #9ca3af; font-family: monospace; letter-spacing: .3px; }
.mm-title { font-size: 14px; font-weight: 500; color: #111827; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.mm-date { font-size: 13px; color: #374151; }
.mm-time-range { font-size: 12px; color: #6b7280; margin-top: 2px; }

.mm-host-name { font-size: 13px; color: #374151; }
.mm-host-dept { font-size: 12px; color: #9ca3af; margin-top: 2px; }

.mm-online { font-size: 11px; background: #e0f2fe; color: #0369a1; padding: 2px 7px; border-radius: 4px; }

/* ── 类型徽章 ── */
.mm-cat { display: inline-block; font-size: 11px; padding: 2px 10px; border-radius: 20px; font-weight: 500; white-space: nowrap; }
.cat-生产 { background: #d1fae5; color: #065f46; }
.cat-周边 { background: #dbeafe; color: #1e40af; }
.cat-研发 { background: #ede9fe; color: #5b21b6; }
.cat-业务 { background: #fef3c7; color: #92400e; }

/* ── 状态徽章 ── */
.mm-status { display: inline-block; font-size: 11px; padding: 3px 11px; border-radius: 20px; font-weight: 600; white-space: nowrap; }
.st-0 { background: #dbeafe; color: #1d4ed8; }
.st-1 { background: #f3f4f6; color: #6b7280; }
.st-2 { background: #d1fae5; color: #065f46; }

/* ── 操作按钮 ── */
.mm-td.th-actions { display: flex; align-items: center; }
.mm-td.th-actions > * + * { margin-left: 5px; }
.mm-btn {
  display: inline-flex; align-items: center;
  font-size: 12px; padding: 4px 11px; border-radius: 5px;
  border: none; cursor: pointer; font-weight: 500; line-height: 1.5;
  white-space: nowrap; transition: opacity .15s;
}
.mm-btn:hover { opacity: .82; }
.mm-btn-default { background: #f3f4f6; color: #374151; border: 1px solid #e5e7eb; }
.mm-btn-danger  { background: #ef4444; color: #fff; }
.mm-btn-warning { background: #f59e0b; color: #fff; }
.mm-btn-primary { background: #2563eb; color: #fff; }

.mm-empty { text-align: center; color: #9ca3af; padding: 48px 0; font-size: 13px; }

/* ── 详情弹窗 ── */
.dd-header { display: flex; flex-direction: column; }
.dd-header > * + * { margin-top: 6px; }
.dd-title { font-size: 18px; font-weight: 700; color: #1f2937; line-height: 1.3; }
.dd-tags { display: flex; align-items: center; flex-wrap: wrap; margin-left: -6px; margin-top: -6px; }
.dd-tags > * { margin-left: 6px; margin-top: 6px; }

.dd-status-tag {
  display: inline-block; font-size: 11px; padding: 2px 10px;
  border-radius: 20px; font-weight: 600;
}
.status-0 { background: #dbeafe; color: #1d4ed8; }
.status-1 { background: #f3f4f6; color: #6b7280; }
.status-2 { background: #d1fae5; color: #065f46; }

.dd-freq {
  display: inline-block; font-size: 11px; padding: 2px 10px;
  border-radius: 20px; background: #f3f4f6; color: #6b7280; border: 1px solid #e5e7eb;
}

.dd-time-banner {
  display: flex; align-items: center; justify-content: space-around;
  background: #f0f5ff; border: 1px solid #c7d7fb; border-radius: 8px;
  padding: 14px 20px; margin-bottom: 16px;
}
.dd-time-block { text-align: center; }
.dd-time-label { font-size: 11px; color: #6b7280; margin-bottom: 4px; }
.dd-time-val { font-size: 15px; font-weight: 600; color: #1e3a8a; }
.dd-time-arrow { font-size: 20px; color: #93c5fd; }

.dd-info-list { margin-bottom: 16px; }
.dd-info-row {
  display: flex; align-items: flex-start;
  padding: 8px 0; border-bottom: 1px solid #f3f4f6; font-size: 13px;
}
.dd-info-row > * + * { margin-left: 10px; }
.dd-info-row:last-child { border-bottom: none; }
.dd-info-label { width: 80px; flex-shrink: 0; color: #9ca3af; }
.dd-info-label i { margin-right: 4px; }
.dd-info-val { flex: 1; color: #1f2937; font-weight: 500; line-height: 1.5; }

.dd-attendees-section {
  background: #f9fafb; border-radius: 8px; padding: 12px 14px; margin-bottom: 4px;
}
.dd-section-title { font-size: 12px; font-weight: 600; color: #6b7280; margin-bottom: 10px; }

.dd-attendees { display: flex; flex-wrap: wrap; margin-left: -8px; margin-top: -8px; }
.dd-attendees > * { margin-left: 8px; margin-top: 8px; }
.dd-attendee {
  display: inline-flex; align-items: center;
  font-size: 13px; padding: 4px 12px; border-radius: 20px;
  background: #fff; border: 1px solid #e5e7eb; color: #374151; font-weight: 500;
}
.dd-attendee > * + * { margin-left: 4px; }
.dd-attendee.is-delegate { background: #eff6ff; border-color: #bfdbfe; color: #1d4ed8; }

.dd-empty-small { font-size: 13px; color: #9ca3af; }

.dd-footer {
  display: flex; align-items: center; justify-content: space-between;
}
.dd-footer > div { display: flex; }
.dd-footer > div > * + * { margin-left: 8px; }
.mm-flex-row { display: flex; align-items: center; }

/* ── 复制预约弹窗 ── */
.bk-steps { display:flex; border:1px solid #e5e7eb; border-radius:8px; overflow:hidden; margin-bottom:20px; }
.bk-step { flex:1; display:flex; flex-direction:column; align-items:center; padding:14px 12px; background:#f9fafb; }
.bk-step.is-active { background:#eef2ff; border-bottom:3px solid #2563eb; }
.bk-step.is-done   { background:#f0fdf4; border-bottom:3px solid #16a34a; }
.bk-step-circle { width:26px; height:26px; border-radius:50%; background:#d1d5db; color:#fff; display:flex; align-items:center; justify-content:center; font-size:13px; font-weight:700; margin-bottom:6px; }
.bk-step.is-active .bk-step-circle { background:#2563eb; }
.bk-step.is-done   .bk-step-circle { background:#16a34a; }
.bk-step-label { font-size:13px; color:#6b7280; text-align:center; }
.bk-step.is-active .bk-step-label { color:#2563eb; font-weight:600; }
.bk-step.is-done   .bk-step-label { color:#16a34a; }
.cp-source-bar { display:flex; flex-wrap:wrap; gap:20px; background:#f0f5ff; border:1px solid #c7d7fb; border-radius:8px; padding:12px 18px; font-size:13px; color:#374151; }
.cp-src-item b { color:#1e3a8a; }
.cp-tl-topbar { display:flex; align-items:center; justify-content:space-between; margin-bottom:10px; }
.cp-tl-info { font-size:13px; font-weight:500; color:#374151; }
.cp-flex-12 { display:flex; align-items:center; gap:12px; }
.cp-leg { display:flex; align-items:center; gap:5px; font-size:12px; color:#6b7280; }
.cp-swatch { display:inline-block; width:16px; height:10px; border-radius:3px; }
.cp-swatch-occ { background:repeating-linear-gradient(45deg,#d1d5db,#d1d5db 3px,#e5e7eb 3px,#e5e7eb 7px); border:1px solid #9ca3af; }
.cp-swatch-sel { background:#2563eb; }
.cp-timeline { min-width:700px; overflow-x:auto; border:1px solid #e5e7eb; border-radius:8px; overflow:hidden; }
.cp-head { display:flex; background:#f9fafb; border-bottom:2px solid #e5e7eb; padding:8px 0; }
.cp-room-col { width:140px; flex-shrink:0; padding:0 10px; display:flex; align-items:center; font-size:12px; color:#6b7280; font-weight:600; }
.cp-hours { flex:1; display:flex; }
.cp-hour { flex:1; font-size:11px; color:#9ca3af; text-align:left; padding-left:4px; }
.cp-row { display:flex; align-items:stretch; border-bottom:1px solid #f3f4f6; min-height:60px; }
.cp-row:last-child { border-bottom:none; }
.cp-room-col { flex-direction:column; justify-content:center; align-items:flex-start; }
.cp-rnum { font-size:13px; font-weight:600; color:#374151; }
.cp-rinfo { font-size:11px; color:#9ca3af; }
.cp-track { flex:1; position:relative; background:#fafafa; cursor:crosshair; }
.cp-vline { position:absolute; top:0; bottom:0; width:1px; background:#e5e7eb; pointer-events:none; }
.cp-occ { position:absolute; top:5px; bottom:5px; border-radius:4px; background:repeating-linear-gradient(45deg,#d1d5db,#d1d5db 4px,#e5e7eb 4px,#e5e7eb 10px); border:1px solid #9ca3af; pointer-events:none; }
.cp-sel { position:absolute; top:5px; bottom:5px; border-radius:4px; background:#2563eb; opacity:.85; pointer-events:none; display:flex; align-items:center; justify-content:center; }
.cp-sel-txt { font-size:11px; color:#fff; font-weight:600; white-space:nowrap; }
.cp-drag-preview { opacity:.5; }
.cp-meta-form { margin-top:14px; }
.cp-sel-bar { display:flex; align-items:center; justify-content:space-between; background:#f0fdf4; border:1px solid #86efac; border-radius:6px; padding:10px 16px; margin-top:4px; }
.cp-sel-bar-text { font-size:13px; color:#166534; }
.cp-sel-bar-text b { font-weight:700; }
.cp-sel-placeholder { font-size:13px; color:#9ca3af; font-style:italic; }
</style>
