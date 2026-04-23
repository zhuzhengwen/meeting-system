<template>
  <div class="app-container">
    <!-- 页头 -->
    <div class="mms-page-header">
      <h2 style="margin:0;font-size:20px;color:#1f2937">会议排程</h2>
      <div style="display:flex;align-items:center;gap:8px;flex-wrap:wrap">
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
    <el-dialog :title="detail.title" :visible.sync="detailVisible" width="500px" append-to-body>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="园区/会议室">
          <template v-if="detail.campus">{{ detail.campus }} {{ detail.roomNumber }}</template>
          <template v-else>线上会议</template>
        </el-descriptions-item>
        <el-descriptions-item label="会议类型">
          <span :class="['mms-cat-badge', 'cat-' + detail.category]">{{ detail.category }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDatetime(detail.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDatetime(detail.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="主持人">{{ detail.hostName }}</el-descriptions-item>
        <el-descriptions-item label="主导部门">{{ detail.leadDept }}</el-descriptions-item>
        <el-descriptions-item label="频率">{{ detail.frequency }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusLabel(detail.status) }}</el-descriptions-item>
        <el-descriptions-item label="会议说明" :span="2">{{ detail.description || '—' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getMeetingSchedule } from '@/api/mms/meeting'

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
      detail: {}
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
      return { '0': '已确认', '1': '已取消', '2': '已完成' }[s] || s
    },
    showDetail(m) {
      this.detail = m
      this.detailVisible = true
    }
  }
}
</script>

<style scoped>
.mms-page-header {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; flex-wrap: wrap; gap: 10px;
}
.mms-legend {
  display: flex; align-items: center; gap: 10px; flex-wrap: wrap; margin-bottom: 14px;
}
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
  display: flex; flex-direction: column; justify-content: center; gap: 3px;
  transition: filter .15s;
}
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
</style>
