<template>
  <div class="mms-dash">

    <!-- ① 欢迎横幅 -->
    <div class="mms-welcome">
      <div class="mms-welcome-left">
        <div class="mms-greeting">{{ greetingText }}，<span class="mms-uname">{{ $store.getters.name }}</span></div>
        <div class="mms-today-str">{{ todayLabel }}</div>
      </div>
      <div class="mms-actions">
        <router-link v-hasPermi="['mms:meeting:add']" to="/mms/booking">
          <el-button type="primary" size="small" icon="el-icon-circle-plus-outline">预约会议</el-button>
        </router-link>
        <router-link v-hasPermi="['mms:meeting:list']" to="/mms/schedule">
          <el-button size="small" icon="el-icon-date">会议排程</el-button>
        </router-link>
        <router-link v-hasPermi="['mms:meeting:list']" to="/mms/search">
          <el-button size="small" icon="el-icon-search">会议查询</el-button>
        </router-link>
      </div>
    </div>

    <!-- ② KPI 卡片 -->
    <div class="mms-kpi-row">
      <div v-for="k in kpis" :key="k.label" class="mms-kpi-card" :style="{'--c': k.color, '--clight': k.light}">
        <div class="mms-kpi-icon-wrap"><i :class="k.icon"></i></div>
        <div class="mms-kpi-body">
          <div class="mms-kpi-val">{{ k.value }}</div>
          <div class="mms-kpi-label">{{ k.label }}</div>
        </div>
      </div>
    </div>

    <!-- ③ 主内容区 -->
    <div class="mms-grid">

      <!-- 左列：日历 + 待跟进 -->
      <div class="mms-left-col">
      <div class="mms-panel mms-cal-panel">
        <div class="mms-panel-hdr">
          <span class="mms-panel-title">近2日我的会议</span>
          <div class="mms-day-badges">
            <span class="mms-day-badge">{{ dayTabLabel(0) }}</span>
            <span class="mms-day-badge mms-day-dim">{{ dayTabLabel(1) }}</span>
          </div>
        </div>

        <div class="mms-cal-head">
          <div class="mms-time-pad"></div>
          <div class="mms-col-hd">今天</div>
          <div class="mms-col-hd mms-col-hd-dim">明天</div>
        </div>

        <div class="mms-cal-body" ref="calBody">
          <div class="mms-time-col">
            <div v-for="h in hourList" :key="h" class="mms-time-cell">
              {{ String(h).padStart(2,'0') }}:00
            </div>
          </div>
          <div
            v-for="(dMtgs, di) in [todayMeetings, tomorrowMeetings]"
            :key="di"
            class="mms-day-col"
          >
            <div class="mms-day-inner" :style="{height: totalCalH + 'px'}">
              <div v-for="h in hourList" :key="h" class="mms-hr-line"
                :style="{top: (h - startHour) * hourH + 'px'}"></div>
              <div v-if="di === 0 && nowTop >= 0" class="mms-now-line"
                :style="{top: nowTop + 'px'}">
                <span class="mms-now-dot"></span>
              </div>
              <div
                v-for="m in dMtgs" :key="m.meetingId"
                :class="['mms-evt', di === 1 && 'mms-evt-dim']"
                :style="evtStyle(m)" :title="m.title"
              >
                <div class="mms-evt-title">{{ m.title }}</div>
                <div class="mms-evt-sub">
                  {{ formatTime(m.startTime) }}–{{ formatTime(m.endTime) }}
                  <template v-if="m.roomNumber"> · {{ m.campus }}{{ m.roomNumber }}</template>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      </div><!-- /mms-left-col -->

      <!-- 右列 -->
      <div class="mms-right">

        <!-- 会议室占用状态 -->
        <div class="mms-panel mms-room-panel">
          <div class="mms-panel-hdr">
            <span class="mms-panel-title">会议室当前状态</span>
            <span class="mms-now-badge">实时</span>
          </div>
          <div v-if="!allRoomsData.length" class="mms-empty">暂无会议室数据</div>
          <div class="mms-room-grid mms-room-grid-scroll">
            <div
              v-for="room in allRoomsData"
              :key="room.roomId"
              :class="['mms-room-chip', currentOccupied.has(room.roomId) ? 'mms-room-busy' : 'mms-room-free']"
            >
              <span class="mms-room-dot"></span>
              <div class="mms-room-info">
                <div class="mms-room-num">{{ room.roomNumber }}</div>
                <div class="mms-room-cap">{{ room.campus }} · {{ room.capacity }}人</div>
              </div>
              <span class="mms-room-status-tag">{{ currentOccupied.has(room.roomId) ? '使用中' : '空闲' }}</span>
            </div>
          </div>
        </div>

        <!-- 定期会议建议 -->
        <div class="mms-panel mms-periodic-panel">
          <div class="mms-panel-hdr">
            <span class="mms-panel-title">定期会议建议</span>
            <router-link to="/mms/schedule">
              <el-button size="mini" type="text">查看排程 →</el-button>
            </router-link>
          </div>
          <div v-if="!periodicMeetings.length" class="mms-empty">暂无定期会议记录</div>
          <div class="mms-periodic-scroll">
          <div
            v-for="m in periodicMeetings"
            :key="m.meetingId"
            class="mms-periodic-row"
          >
            <div class="mms-periodic-left">
              <span class="mms-ptag" :class="'mms-cat-' + m.category">{{ m.category }}</span>
              <span class="mms-ptag mms-freq-tag">{{ freqLabel(m.frequency) }}</span>
            </div>
            <div class="mms-periodic-mid">
              <div class="mms-periodic-title">{{ m.title }}</div>
              <div class="mms-periodic-meta">{{ m.hostName || m.createBy }} · {{ m.leadDept }}</div>
            </div>
            <div class="mms-periodic-right">
              <div class="mms-periodic-ago">{{ daysAgoStr(m.startTime) }}</div>
              <div class="mms-periodic-date">{{ shortDate(m.startTime) }}</div>
            </div>
          </div>
          </div>
        </div>

      </div>
    </div>

    <!-- ④ 待跟进事项（全宽） -->
    <div class="mms-panel mms-tracking-panel" style="margin-top:16px">
      <div class="mms-panel-hdr">
        <span class="mms-panel-title">待跟进事项</span>
        <router-link to="/mms/tracking">
          <el-button size="mini" type="text">查看全部 →</el-button>
        </router-link>
      </div>
      <div v-if="!trackingItems.length" class="mms-empty">暂无待跟进事项</div>
      <div v-else class="mms-tk-list">
        <div v-for="(row, idx) in trackingItems" :key="row.trackingId" class="mms-tk-card">
          <div class="mms-tk-seq">{{ idx + 1 }}</div>
          <div class="mms-tk-body">
            <div class="mms-tk-toprow">
              <div class="mms-tk-tags">
                <span v-if="row.category" class="mms-ptag" :class="'mms-cat-' + row.category">{{ row.category }}</span>
                <span v-if="row.frequency" class="mms-ptag mms-freq-tag">{{ freqLabel(row.frequency) }}</span>
                <span v-if="isOverdue(row)" class="mms-ptag mms-tk-overdue">已逾期</span>
              </div>
              <div class="mms-tk-right">
                <span v-if="row.plannedDate" class="mms-tk-date" :class="isOverdue(row) ? 'mms-tk-date-red' : ''">
                  <i class="el-icon-date"></i> {{ row.plannedDate }}
                </span>
                <el-button size="mini" type="primary" @click="goTracking(row)">更新进度</el-button>
              </div>
            </div>
            <div class="mms-tk-title">{{ row.project }}</div>
            <div class="mms-tk-meta">
              <template v-if="row.meetingTitle"><i class="el-icon-s-order"></i> {{ row.meetingTitle }} · </template>
              <template v-if="row.trackDept">跟踪：{{ row.trackDept }} · </template>
              <template v-if="row.leadDept">主导：{{ row.leadDept }}</template>
            </div>
            <div v-if="row.directorNote" class="mms-tk-hint">
              <i class="el-icon-location-information mms-tk-pin"></i>
              <span class="mms-tk-hint-label">会议主管指示</span>
              <span class="mms-tk-hint-text">{{ row.directorNote }}</span>
            </div>
            <div v-if="row.progressList && row.progressList.length" class="mms-tk-progress">
              <i class="el-icon-document"></i>
              <span v-for="(p, i) in row.progressList.slice(-1)" :key="i">
                {{ p.progressDate }}：{{ p.content }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import { getMeetingSchedule, listMeeting } from '@/api/mms/meeting'
import { allRooms } from '@/api/mms/room'
import { listTracking, addProgress } from '@/api/mms/tracking'

const START_HOUR = 8
const END_HOUR = 21
const HOUR_H = 36

const CAT_COLORS = {
  '生产': '#16a34a', '研发': '#7c3aed', '业务': '#d97706',
  '周边': '#2563eb', '信息技术': '#0891b2', 'IT': '#0891b2'
}

function todayStr(offset = 0) {
  const d = new Date()
  d.setDate(d.getDate() + offset)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}

export default {
  name: 'MmsDashboard',
  data() {
    return {
      stats: { totalRooms: 0, freeRooms: 0, todayMeetings: 0, myMeetings: 0, openTracking: 0 },
      todayMeetings: [],
      tomorrowMeetings: [],
      periodicMeetings: [],
      allRoomsData: [],
      trackingItems: [],
      currentOccupied: new Set(),
      startHour: START_HOUR,
      endHour: END_HOUR,
      hourH: HOUR_H,
      nowTop: -1,
      nowTimer: null
    }
  },
  computed: {
    greetingText() {
      const h = new Date().getHours()
      if (h < 6)  return '夜深了'
      if (h < 12) return '早上好'
      if (h < 18) return '下午好'
      return '晚上好'
    },
    todayLabel() {
      const d = new Date()
      const days = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
      return `${d.getFullYear()}年${d.getMonth()+1}月${d.getDate()}日  ${days[d.getDay()]}`
    },
    kpis() {
      return [
        { label: '会议室总数', value: this.stats.totalRooms,    icon: 'el-icon-office-building', color: '#2563eb', light: '#eff6ff' },
        { label: '当前空闲',   value: this.stats.freeRooms,     icon: 'el-icon-circle-check',    color: '#16a34a', light: '#f0fdf4' },
        { label: '今日全部',   value: this.stats.todayMeetings, icon: 'el-icon-date',             color: '#d97706', light: '#fffbeb' },
        { label: '今日我的',   value: this.stats.myMeetings,    icon: 'el-icon-user',             color: '#9333ea', light: '#faf5ff' },
        { label: '待跟进',     value: this.stats.openTracking,  icon: 'el-icon-warning-outline',  color: '#dc2626', light: '#fef2f2' }
      ]
    },
    hourList() {
      const list = []
      for (let h = this.startHour; h <= this.endHour; h++) list.push(h)
      return list
    },
    totalCalH() {
      return (this.endHour - this.startHour) * this.hourH
    }
  },
  created() {
    this.updateNow()
    this.loadData()
    this.nowTimer = setInterval(this.updateNow, 60000)
  },
  beforeDestroy() {
    if (this.nowTimer) clearInterval(this.nowTimer)
  },
  methods: {
    loadData() {
      const today    = todayStr(0)
      const tomorrow = todayStr(1)
      const username = this.$store.getters.name

      getMeetingSchedule(today).then(res => {
        const all = res.data || []
        this.stats.todayMeetings = all.length
        this.stats.myMeetings = all.filter(
          m => m.hostUser === username || m.createBy === username
        ).length
        this.todayMeetings = this.computeLanes(all)
        const now = new Date()
        this.currentOccupied = new Set(
          all.filter(m => new Date(m.startTime) <= now && new Date(m.endTime) > now)
             .map(m => m.roomId).filter(Boolean)
        )
      })

      getMeetingSchedule(tomorrow).then(res => {
        this.tomorrowMeetings = this.computeLanes(res.data || [])
      })

      allRooms({ status: '0' }).then(res => {
        const rooms = res.data || []
        this.allRoomsData = rooms
        this.stats.totalRooms = rooms.length
        getMeetingSchedule(today).then(res2 => {
          const usedRooms = new Set((res2.data || []).map(m => m.roomId).filter(Boolean))
          this.stats.freeRooms = rooms.filter(r => !usedRooms.has(r.roomId)).length
        })
      })

      listTracking({ status: '0', pageNum: 1, pageSize: 100 }).then(res => {
        const rows = res.rows || []
        this.stats.openTracking = rows.length
        this.trackingItems = rows.slice(0, 5)
      })

      listMeeting({ pageNum: 1, pageSize: 200 }).then(res => {
        const all = (res.rows || []).filter(m => m.frequency && m.frequency !== '')
        all.sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
        const seen = new Set(); const deduped = []
        for (const m of all) {
          if (!seen.has(m.title)) { seen.add(m.title); deduped.push(m) }
        }
        this.periodicMeetings = deduped.slice(0, 6)
      })
    },

    computeLanes(meetings) {
      const sorted = [...meetings].sort((a,b) => new Date(a.startTime)-new Date(b.startTime))
      const laneEnds = []
      const result = sorted.map(m => {
        const sMs = new Date(m.startTime).getTime()
        const eMs = new Date(m.endTime).getTime()
        let lane = laneEnds.findIndex(end => sMs >= end)
        if (lane === -1) { lane = laneEnds.length; laneEnds.push(eMs) } else { laneEnds[lane] = eMs }
        return { ...m, _lane: lane }
      })
      const totalCols = laneEnds.length || 1
      return result.map(m => ({ ...m, _totalCols: totalCols }))
    },

    updateNow() {
      const now = new Date()
      const mins = now.getHours() * 60 + now.getMinutes()
      const startMins = this.startHour * 60
      const endMins   = this.endHour * 60
      this.nowTop = (mins >= startMins && mins <= endMins)
        ? (mins - startMins) / 60 * this.hourH : -1
    },

    evtStyle(m) {
      const s = new Date(m.startTime), e = new Date(m.endTime)
      const sMin = s.getHours()*60+s.getMinutes()
      const eMin = e.getHours()*60+e.getMinutes()
      const top    = (sMin - this.startHour*60) / 60 * this.hourH
      const height = Math.max((eMin-sMin)/60*this.hourH - 2, 18)
      const totalCols = m._totalCols || 1
      const lane      = m._lane || 0
      const color = CAT_COLORS[m.category] || '#3b82f6'
      return {
        top: top+'px', height: height+'px',
        width: `calc(${(100/totalCols).toFixed(1)}% - 6px)`,
        left:  `calc(${((lane/totalCols)*100).toFixed(1)}% + 3px)`,
        background: color
      }
    },

    freqLabel(f) {
      return { '0':'日报','1':'周报','2':'双周报','3':'月报','4':'季报' }[f] || f || ''
    },

    dayTabLabel(offset) {
      const d = new Date(); d.setDate(d.getDate()+offset)
      const days = ['周日','周一','周二','周三','周四','周五','周六']
      return `${d.getMonth()+1}/${d.getDate()} ${days[d.getDay()]}`
    },

    formatTime(ts) {
      if (!ts) return ''
      const d = new Date(ts)
      return `${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
    },

    shortDate(ts) {
      if (!ts) return '-'
      const d = new Date(ts)
      return `${d.getMonth()+1}/${d.getDate()}`
    },

    isOverdue(row) {
      if (!row.plannedDate) return false
      return new Date(row.plannedDate) < new Date(todayStr(0))
    },

    goTracking(row) {
      this.$router.push({ path: '/mms/tracking', query: { trackingId: row.trackingId } })
    },

    daysAgoStr(ts) {
      if (!ts) return ''
      const days = Math.floor((Date.now()-new Date(ts).getTime())/86400000)
      if (days <= 0) return '今天'
      if (days === 1) return '昨天'
      return `${days}天前`
    }
  }
}
</script>

<style scoped>
.mms-dash { padding: 20px; background: #f1f5f9; min-height: 100%; }

/* ── 欢迎横幅 ── */
.mms-welcome {
  display: flex; align-items: center; justify-content: space-between;
  background: linear-gradient(135deg, #2563eb 0%, #60a5fa 100%);
  border-radius: 12px; padding: 20px 28px; margin-bottom: 20px;
  box-shadow: 0 4px 16px rgba(37,99,235,.3);
}
.mms-greeting { font-size: 20px; font-weight: 700; color: #fff; }
.mms-uname { color: #bfdbfe; }
.mms-today-str { font-size: 13px; color: rgba(255,255,255,.65); margin-top: 4px; }
.mms-actions { display: flex; }
.mms-actions > * + * { margin-left: 10px; }
.mms-actions .el-button { border-radius: 6px; }

/* ── KPI 卡片 ── */
.mms-kpi-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  grid-gap: 14px; margin-bottom: 20px;
}
@media (max-width: 1200px) { .mms-kpi-row { grid-template-columns: repeat(3,1fr); } }

.mms-kpi-card {
  background: #fff;
  border-radius: 10px;
  padding: 18px 20px;
  display: flex; align-items: center;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
  border-left: 4px solid var(--c);
  transition: box-shadow .15s;
}
.mms-kpi-card > * + * { margin-left: 14px; }
.mms-kpi-card:hover { box-shadow: 0 4px 14px rgba(0,0,0,.1); }

.mms-kpi-icon-wrap {
  width: 46px; height: 46px; border-radius: 10px;
  background: var(--clight); color: var(--c);
  display: flex; align-items: center; justify-content: center;
  font-size: 22px; flex-shrink: 0;
}
.mms-kpi-val   { font-size: 28px; font-weight: 700; color: #111827; line-height: 1.1; }
.mms-kpi-label { font-size: 12px; color: #6b7280; margin-top: 3px; }

/* ── 主内容网格 ── */
.mms-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  grid-gap: 16px; align-items: start;
}
@media (max-width: 1300px) { .mms-grid { grid-template-columns: 1fr; } }

/* ── 通用面板 ── */
.mms-panel {
  background: #fff; border-radius: 10px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 4px rgba(0,0,0,.05);
  overflow: hidden;
}
.mms-panel-hdr {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 18px 12px;
  border-bottom: 1px solid #f3f4f6;
}
.mms-panel-title { font-size: 14px; font-weight: 600; color: #111827; }
.mms-empty { text-align: center; color: #9ca3af; font-size: 13px; padding: 28px 0; }

/* ── 左列容器 ── */
.mms-left-col { display: flex; flex-direction: column; min-width: 0; }

/* ── 日历面板 ── */
.mms-cal-panel { display: flex; flex-direction: column; height: 600px; overflow: hidden; }

.mms-day-badges { display: flex; }
.mms-day-badges > * + * { margin-left: 6px; }
.mms-day-badge {
  font-size: 12px; color: #374151;
  background: #eff6ff; color: #1d4ed8;
  padding: 2px 10px; border-radius: 20px;
}
.mms-day-dim { background: #f9fafb; color: #9ca3af; }

.mms-cal-head {
  display: flex; padding: 8px 0 6px;
  border-bottom: 1px solid #f3f4f6;
  margin: 0 18px;
}
.mms-time-pad  { width: 44px; flex-shrink: 0; }
.mms-col-hd {
  flex: 1; text-align: center;
  font-size: 13px; font-weight: 600; color: #374151;
}
.mms-col-hd-dim { color: #9ca3af; }

.mms-cal-body { display: flex; padding: 0 18px 16px; }

.mms-time-col { width: 44px; flex-shrink: 0; }
.mms-time-cell {
  height: 36px; display: flex; align-items: flex-start;
  justify-content: flex-end; padding-right: 8px; padding-top: 3px;
  font-size: 11px; color: #9ca3af;
}

.mms-day-col { flex: 1; border-left: 1px solid #f0f0f0; min-width: 0; }
.mms-day-inner { position: relative; }

.mms-hr-line { position: absolute; left: 0; right: 0; height: 1px; background: #f3f4f6; pointer-events: none; }

.mms-now-line {
  position: absolute; left: 0; right: 0; height: 2px;
  background: #ef4444; z-index: 10;
}
.mms-now-dot {
  position: absolute; left: -4px; top: -4px;
  width: 10px; height: 10px; border-radius: 50%; background: #ef4444;
}

.mms-evt {
  position: absolute; border-radius: 5px;
  padding: 4px 7px; overflow: hidden; cursor: pointer; z-index: 5;
  transition: opacity .15s, transform .1s;
}
.mms-evt:hover { opacity: .88; transform: scale(1.01); }
.mms-evt-dim { opacity: .4; filter: grayscale(30%); }
.mms-evt-title {
  font-size: 12px; font-weight: 600; color: #fff;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.mms-evt-sub {
  font-size: 11px; color: rgba(255,255,255,.82); margin-top: 2px;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}

/* ── 右列 ── */
.mms-right { display: flex; flex-direction: column; height: 600px; }
.mms-right > * + * { margin-top: 16px; }
.mms-right .mms-panel { flex: 1; display: flex; flex-direction: column; overflow: hidden; min-height: 0; }

/* ── 会议室状态 ── */
.mms-now-badge {
  font-size: 11px; font-weight: 600;
  color: #16a34a; background: #f0fdf4;
  border: 1px solid #bbf7d0;
  padding: 2px 8px; border-radius: 20px;
}
.mms-room-grid {
  padding: 12px 16px 14px;
  display: flex; flex-direction: column;
}
.mms-room-grid > * + * { margin-top: 6px; }
.mms-room-grid-scroll { flex: 1; overflow-y: auto; }
.mms-room-chip {
  display: flex; align-items: center;
  padding: 8px 12px; border-radius: 8px; border: 1px solid;
  transition: background .15s;
}
.mms-room-chip > * + * { margin-left: 10px; }
.mms-room-free { background: #f0fdf4; border-color: #bbf7d0; }
.mms-room-busy { background: #fef2f2; border-color: #fecaca; }

.mms-room-dot {
  width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0;
}
.mms-room-free .mms-room-dot { background: #16a34a; box-shadow: 0 0 5px #16a34a; }
.mms-room-busy .mms-room-dot { background: #ef4444; box-shadow: 0 0 5px #ef4444; }

.mms-room-info { flex: 1; min-width: 0; }
.mms-room-num  { font-size: 13px; font-weight: 600; color: #111827; }
.mms-room-cap  { font-size: 11px; color: #6b7280; }

.mms-room-status-tag {
  font-size: 11px; font-weight: 600; flex-shrink: 0;
}
.mms-room-free .mms-room-status-tag { color: #16a34a; }
.mms-room-busy .mms-room-status-tag { color: #ef4444; }

/* ── 定期建议 ── */
.mms-periodic-scroll { flex: 1; overflow-y: auto; }
.mms-periodic-row {
  display: flex; align-items: center;
  padding: 10px 18px;
  border-bottom: 1px solid #f9fafb;
  transition: background .12s;
}
.mms-periodic-row > * + * { margin-left: 10px; }
.mms-periodic-row:last-child { border-bottom: none; }
.mms-periodic-row:hover { background: #f9fafb; }

.mms-periodic-left { display: flex; flex-shrink: 0; }
.mms-periodic-left > * + * { margin-left: 4px; }
.mms-periodic-mid  { flex: 1; min-width: 0; }
.mms-periodic-right { text-align: right; flex-shrink: 0; }

.mms-ptag {
  display: inline-block; font-size: 11px; font-weight: 500;
  padding: 1px 7px; border-radius: 20px; line-height: 18px; white-space: nowrap;
}
.mms-cat-研发  { background: #ede9fe; color: #5b21b6; }
.mms-cat-业务  { background: #fef3c7; color: #92400e; }
.mms-cat-生产  { background: #d1fae5; color: #065f46; }
.mms-cat-周边  { background: #dbeafe; color: #1e40af; }
.mms-cat-信息技术 { background: #ccfbf1; color: #0f766e; }
.mms-freq-tag  { background: #f3f4f6; color: #4b5563; }

.mms-periodic-title {
  font-size: 13px; font-weight: 600; color: #111827;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.mms-periodic-meta { font-size: 11px; color: #9ca3af; margin-top: 2px; }

.mms-periodic-ago  { font-size: 11px; font-weight: 600; color: #ef4444; }
.mms-periodic-date { font-size: 11px; color: #6b7280; margin-top: 2px; }

/* ── 待跟进事项 ── */
.mms-tracking-panel { }
.mms-tk-list { padding: 8px 16px 12px; display: flex; flex-direction: column; }
.mms-tk-list > * + * { margin-top: 10px; }

.mms-tk-card {
  display: flex; align-items: flex-start;
  padding: 14px 16px; border-radius: 8px;
  border: 1px solid #e5e7eb;
  background: #fff;
  transition: box-shadow .15s;
}
.mms-tk-card:hover { box-shadow: 0 2px 10px rgba(0,0,0,.08); }

.mms-tk-seq {
  width: 28px; height: 28px; border-radius: 50%;
  background: #2563eb; color: #fff;
  font-size: 13px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; margin-right: 14px; margin-top: 2px;
}

.mms-tk-body { flex: 1; min-width: 0; }

.mms-tk-toprow {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 6px;
}
.mms-tk-tags { display: flex; flex-wrap: wrap; gap: 4px; }

.mms-tk-overdue { background: #fee2e2; color: #dc2626; }

.mms-tk-right { display: flex; align-items: center; flex-shrink: 0; margin-left: 12px; }
.mms-tk-right > * + * { margin-left: 8px; }

.mms-tk-date { font-size: 12px; color: #6b7280; white-space: nowrap; }
.mms-tk-date-red { color: #dc2626; font-weight: 600; }

.mms-tk-title {
  font-size: 14px; font-weight: 600; color: #111827;
  margin-bottom: 4px;
}
.mms-tk-meta {
  font-size: 12px; color: #9ca3af; margin-bottom: 6px;
}
.mms-tk-meta i { margin-right: 3px; }

.mms-tk-hint {
  display: flex; align-items: flex-start;
  background: #fffbeb; border: 1px solid #fde68a;
  border-radius: 6px; padding: 7px 10px;
  font-size: 12px; color: #78350f;
  margin-bottom: 6px;
}
.mms-tk-pin { color: #d97706; font-size: 14px; flex-shrink: 0; margin-right: 5px; margin-top: 1px; }
.mms-tk-hint-label { font-weight: 600; flex-shrink: 0; margin-right: 6px; }
.mms-tk-hint-text { color: #92400e; line-height: 1.5; }

.mms-tk-progress {
  font-size: 12px; color: #6b7280; display: flex; align-items: flex-start;
}
.mms-tk-progress i { margin-right: 4px; flex-shrink: 0; margin-top: 2px; }
</style>
