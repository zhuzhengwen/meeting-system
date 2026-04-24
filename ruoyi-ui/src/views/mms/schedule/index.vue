<template>
  <div class="app-container">
    <!-- 页头 -->
    <div class="mms-page-header">
      <h2 style="margin:0;font-size:20px;color:#1f2937">会议排程</h2>
      <div class="mms-nav-row">
        <el-button size="small" icon="el-icon-arrow-left" @click="changeDate(-1)">前一天</el-button>
        <el-date-picker v-model="scheduleDate" type="date" size="small" value-format="yyyy-MM-dd"
          style="width:150px" @change="loadSchedule" />
        <el-button size="small" @click="changeDate(1)">后一天 <i class="el-icon-arrow-right"></i></el-button>
        <el-button size="small" type="primary" @click="$router.push('/mms/booking')">+ 预约会议</el-button>
      </div>
    </div>

    <!-- 分类图例 -->
    <div class="mms-legend">
      <span class="mms-legend-label">会议类型：</span>
      <span class="mms-cat-badge cat-生产">生产</span>
      <span class="mms-cat-badge cat-周边">周边</span>
      <span class="mms-cat-badge cat-研发">研发</span>
      <span class="mms-cat-badge cat-业务">业务</span>
    </div>

    <!-- 园区标签 -->
    <el-tabs v-model="activeCampus" @tab-click="filterByCampus">
      <el-tab-pane label="全部" name="all"></el-tab-pane>
      <el-tab-pane label="1园" name="1园"></el-tab-pane>
      <el-tab-pane label="2园" name="2园"></el-tab-pane>
      <el-tab-pane label="3园" name="3园"></el-tab-pane>
    </el-tabs>

    <!-- 时间线 -->
    <el-card v-loading="loading" shadow="never" style="overflow-x:auto">
      <div class="mms-timeline">
        <!-- 时间轴头部 -->
        <div class="mms-tl-header">
          <div class="mms-tl-room-col">会议室</div>
          <div class="mms-tl-hours">
            <div v-for="h in hours" :key="h" class="mms-tl-hour">{{ h }}:00</div>
          </div>
        </div>

        <!-- 每行会议室 -->
        <div v-for="room in filteredRooms" :key="room.key" class="mms-tl-row">
          <div class="mms-tl-room-col">
            <span class="mms-room-label">{{ room.key }}</span>
          </div>
          <div class="mms-tl-track">
            <!-- 分隔线 -->
            <div v-for="h in hours" :key="h" class="mms-tl-grid-line"
              :style="{ left: hourToPercent(h) + '%' }"></div>
            <!-- 当前时间线 -->
            <div v-if="showNowLine" class="mms-now-line" :style="{ left: nowPercent + '%' }"></div>
            <!-- 会议块 -->
            <div v-for="m in getMeetingsForRoom(room.key)" :key="m.meetingId"
              :class="['mms-meeting-block', 'cat-bg-' + m.category]"
              :style="meetingBlockStyle(m)"
              @click="showDetail(m)">
              <div class="mms-block-title">{{ m.title }}</div>
              <div class="mms-block-time">{{ formatTime(m.startTime) }}–{{ formatTime(m.endTime) }}</div>
            </div>
          </div>
        </div>

        <div v-if="filteredRooms.length === 0" class="mms-empty-timeline">
          当日暂无会议安排
        </div>
      </div>
    </el-card>

    <!-- 会议详情弹窗 -->
    <el-dialog :visible.sync="detailVisible" width="580px" append-to-body>
      <div slot="title" class="dd-header">
        <div class="dd-title">{{ detailData.title }}</div>
        <div class="dd-tags">
          <span :class="['mms-cat-badge', 'cat-' + detailData.category]">{{ detailData.category }}</span>
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
          <el-button v-if="detailData.status==='0'" size="small" type="warning"
            icon="el-icon-close" @click="handleCancel(detailData)">取消会议</el-button>
          <el-button v-if="detailData.status==='0'" size="small" type="success"
            icon="el-icon-check" @click="handleComplete(detailData)">标记完成</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMeetingSchedule, getMeeting, cancelMeeting, completeMeeting } from '@/api/mms/meeting'

export default {
  name: 'MmsSchedule',
  data() {
    const today = new Date()
    return {
      loading: false,
      scheduleDate: `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}-${String(today.getDate()).padStart(2,'0')}`,
      activeCampus: 'all',
      meetings: [],
      filteredRooms: [],
      hours: [8,9,10,11,12,13,14,15,16,17,18,19,20],
      startHour: 8,
      endHour: 21,
      detailVisible: false,
      detailData: {}
    }
  },
  computed: {
    nowPercent() {
      const now = new Date()
      const mins = (now.getHours() - this.startHour) * 60 + now.getMinutes()
      const total = (this.endHour - this.startHour) * 60
      return Math.max(0, Math.min(100, (mins / total) * 100))
    },
    showNowLine() {
      const today = new Date()
      const ds = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}-${String(today.getDate()).padStart(2,'0')}`
      return ds === this.scheduleDate
    }
  },
  created() { this.loadSchedule() },
  methods: {
    loadSchedule() {
      this.loading = true
      getMeetingSchedule(this.scheduleDate).then(res => {
        this.meetings = res.data || []
        this.buildRooms()
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    buildRooms() {
      const campus = this.activeCampus === 'all' ? null : this.activeCampus
      const filtered = campus ? this.meetings.filter(m => m.campus === campus) : this.meetings
      const roomSet = new Set()
      filtered.forEach(m => { if (m.campus && m.roomNumber) roomSet.add(m.campus + m.roomNumber) })
      this.filteredRooms = Array.from(roomSet).sort().map(key => ({ key }))
    },
    filterByCampus() { this.buildRooms() },
    changeDate(delta) {
      const d = new Date(this.scheduleDate)
      d.setDate(d.getDate() + delta)
      this.scheduleDate = `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
      this.loadSchedule()
    },
    getMeetingsForRoom(room) {
      return this.meetings.filter(m => m.campus && m.roomNumber && (m.campus + m.roomNumber) === room)
    },
    hourToPercent(h) {
      return ((h - this.startHour) / (this.endHour - this.startHour)) * 100
    },
    meetingBlockStyle(m) {
      const start = new Date(m.startTime)
      const end = new Date(m.endTime)
      const startMins = (start.getHours() - this.startHour) * 60 + start.getMinutes()
      const endMins = (end.getHours() - this.startHour) * 60 + end.getMinutes()
      const total = (this.endHour - this.startHour) * 60
      const left = Math.max(0, (startMins / total) * 100)
      const width = Math.max(1, ((endMins - startMins) / total) * 100)
      return { left: left + '%', width: width + '%' }
    },
    formatTime(ts) {
      if (!ts) return ''
      const d = new Date(ts)
      return `${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
    },
    formatDatetime(ts) {
      if (!ts) return ''
      const d = new Date(ts)
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${this.formatTime(ts)}`
    },
    statusLabel(s) {
      return { '0': '已确认', '1': '已取消', '2': '已完成', '3': '待审批', '4': '审批拒绝' }[s] || s
    },
    showDetail(m) {
      getMeeting(m.meetingId).then(res => {
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
        this.loadSchedule()
      })
    },
    handleComplete(row) {
      this.$confirm(`确定将"${row.title}"标记为已完成吗？`, '提示', { type: 'warning' }).then(() => {
        return completeMeeting(row.meetingId)
      }).then(() => {
        this.msgSuccess('已完成')
        this.detailVisible = false
        this.loadSchedule()
      })
    }
  }
}
</script>

<style scoped>
.mms-page-header {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; flex-wrap: wrap; margin-left: -10px; margin-top: -10px;
}
.mms-page-header > * { margin-left: 10px; margin-top: 10px; }
.mms-legend {
  display: flex; align-items: center; flex-wrap: wrap; margin-bottom: 14px; margin-left: -10px; margin-top: -10px;
}
.mms-legend > * { margin-left: 10px; margin-top: 10px; }
.mms-legend-label { font-size: 12px; color: #9ca3af; }
.mms-cat-badge {
  font-size: 12px; padding: 2px 10px; border-radius: 10px; font-weight: 500;
}
.cat-生产 { background: #eaf4ee; color: #2e6347; border: 1px solid #5a9472; }
.cat-周边 { background: #e5eef6; color: #2c5578; border: 1px solid #4d7fa8; }
.cat-研发 { background: #ebe8f5; color: #443478; border: 1px solid #7560a8; }
.cat-业务 { background: #f5ece4; color: #7a3e18; border: 1px solid #b87048; }

.mms-timeline { min-width: 900px; }
.mms-tl-header {
  display: flex; border-bottom: 2px solid #e5e7eb; padding-bottom: 8px; margin-bottom: 4px;
}
.mms-tl-room-col {
  width: 120px; flex-shrink: 0; font-size: 12px; color: #6b7280; font-weight: 600;
  display: flex; align-items: center;
}
.mms-tl-hours {
  flex: 1; display: flex; position: relative;
}
.mms-tl-hour {
  flex: 1; font-size: 11px; color: #9ca3af; text-align: left; padding-left: 4px;
}
.mms-tl-row {
  display: flex; align-items: center; border-bottom: 1px solid #f3f4f6; min-height: 56px;
}
.mms-room-label {
  font-size: 13px; color: #374151; font-weight: 500;
}
.mms-tl-track {
  flex: 1; height: 48px; position: relative; background: #fafafa; border-radius: 4px;
}
.mms-tl-grid-line {
  position: absolute; top: 0; bottom: 0; width: 1px; background: #e5e7eb;
}
.mms-now-line {
  position: absolute; top: 0; bottom: 0; width: 2px; background: #dc2626; z-index: 5;
}
.mms-meeting-block {
  position: absolute; top: 3px; bottom: 3px; border-radius: 4px;
  padding: 4px 8px; overflow: hidden; cursor: pointer; min-width: 4px;
  display: flex; flex-direction: column; justify-content: center;
  transition: filter .15s;
}
.mms-meeting-block > * + * { margin-top: 3px; }
.mms-meeting-block:hover { filter: brightness(.93); }
.mms-block-title {
  font-size: 12px; font-weight: 600; color: #fff;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
  line-height: 1.4;
}
.mms-block-time { font-size: 10px; color: rgba(255,255,255,.85); line-height: 1.4; }
.cat-bg-生产 { background: #5a9472; color: #fff; }
.cat-bg-周边 { background: #4d7fa8; color: #fff; }
.cat-bg-研发 { background: #7560a8; color: #fff; }
.cat-bg-业务 { background: #b87048; color: #fff; }
.mms-empty-timeline { text-align: center; color: #9ca3af; padding: 40px 0; }
.mms-nav-row { display: flex; align-items: center; flex-wrap: wrap; }
.mms-nav-row > * + * { margin-left: 8px; }

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
.status-3 { background: #fef3c7; color: #92400e; }
.status-4 { background: #fee2e2; color: #991b1b; }

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
.dd-attendee.is-delegate { background: #eff6ff; border-color: #bfdbfe; color: #1d4ed8; }
.dd-empty-small { font-size: 13px; color: #9ca3af; }

.dd-footer {
  display: flex; align-items: center; justify-content: space-between;
}
.dd-footer > div { display: flex; }
.dd-footer > div > * + * { margin-left: 8px; }
</style>
