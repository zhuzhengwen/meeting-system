<template>
  <div class="mms-home">

    <!-- 欢迎横幅 -->
    <div class="mms-hero">
      <div class="mms-hero-left">
        <div class="mms-hero-greeting">{{ greeting }}，<span class="mms-hero-name">{{ userName }}</span></div>
        <div class="mms-hero-date">{{ dateLabel }}</div>
        <div class="mms-hero-summary">
          今日共 <b>{{ todayTotal }}</b> 场会议
          <span class="mms-dot-sep">·</span>
          我参与 <b>{{ myTotal }}</b> 场
          <span class="mms-dot-sep">·</span>
          待跟进 <b>{{ openTracking }}</b> 项
        </div>
      </div>
      <div class="mms-hero-right">
        <router-link to="/mms/booking">
          <el-button type="primary" size="medium" icon="el-icon-circle-plus-outline" round>
            立即预约
          </el-button>
        </router-link>
      </div>
      <!-- 装饰 -->
      <div class="mms-hero-deco d1"></div>
      <div class="mms-hero-deco d2"></div>
    </div>

    <!-- 快捷入口 -->
    <div class="mms-section-title">功能入口</div>
    <div class="mms-nav-grid">
      <router-link v-for="nav in navItems" :key="nav.path" :to="nav.path" class="mms-nav-card">
        <div class="mms-nav-icon" :style="{'background': nav.light, 'color': nav.color}">
          <i :class="nav.icon"></i>
        </div>
        <div class="mms-nav-body">
          <div class="mms-nav-title">{{ nav.title }}</div>
          <div class="mms-nav-desc">{{ nav.desc }}</div>
        </div>
        <i class="el-icon-arrow-right mms-nav-arrow"></i>
      </router-link>
    </div>

    <!-- 今日会议 + 待跟进 -->
    <div class="mms-section-title">今日概览</div>
    <div class="mms-overview-grid">

      <!-- 今日会议列表 -->
      <div class="mms-panel">
        <div class="mms-panel-hdr">
          <span class="mms-panel-title">今日所有会议</span>
          <router-link to="/mms/schedule">
            <el-button size="mini" type="text">排程视图 →</el-button>
          </router-link>
        </div>
        <div v-if="!todayMeetings.length" class="mms-empty">今日暂无会议安排</div>
        <div
          v-for="m in todayMeetings.slice(0, 8)"
          :key="m.meetingId"
          class="mms-meeting-row"
          :class="getMeetingState(m)"
        >
          <div class="mms-meeting-time">
            <div class="mms-time-start">{{ fmtTime(m.startTime) }}</div>
            <div class="mms-time-end">{{ fmtTime(m.endTime) }}</div>
          </div>
          <div class="mms-meeting-bar" :style="{'background': catColor(m.category)}"></div>
          <div class="mms-meeting-body">
            <div class="mms-meeting-title">{{ m.title }}</div>
            <div class="mms-meeting-meta">
              <span v-if="m.meetingType === '1'" class="mms-tag-online">线上</span>
              <template v-else>
                <span class="mms-tag-loc">{{ m.campus }} · {{ m.roomNumber }}</span>
              </template>
              <span v-if="m.leadDept" class="mms-tag-dept">{{ m.leadDept }}</span>
            </div>
          </div>
          <span :class="['mms-state-tag', 'mms-state-' + getMeetingState(m)]">
            {{ stateLabel(m) }}
          </span>
        </div>
        <div v-if="todayMeetings.length > 8" class="mms-more-hint">
          还有 {{ todayMeetings.length - 8 }} 场会议
          <router-link to="/mms/search">查看全部</router-link>
        </div>
      </div>

      <!-- 待跟进事项 -->
      <div class="mms-panel">
        <div class="mms-panel-hdr">
          <span class="mms-panel-title">待跟进事项</span>
          <router-link to="/mms/tracking">
            <el-button size="mini" type="text">全部跟踪 →</el-button>
          </router-link>
        </div>
        <div v-if="!trackingList.length" class="mms-empty">暂无待跟进事项</div>
        <div v-for="t in trackingList" :key="t.trackingId" class="mms-tracking-row">
          <div class="mms-tracking-icon">
            <i class="el-icon-warning-outline"></i>
          </div>
          <div class="mms-tracking-body">
            <div class="mms-tracking-title">{{ t.content || t.description }}</div>
            <div class="mms-tracking-meta">
              {{ t.assignee || t.createBy }}
              <span v-if="t.deadline" class="mms-deadline" :class="isOverdue(t.deadline) ? 'mms-overdue' : ''">
                · 截止 {{ fmtDate(t.deadline) }}
              </span>
            </div>
          </div>
          <span class="mms-tracking-from">{{ t.meetingTitle || '—' }}</span>
        </div>
      </div>

    </div>

  </div>
</template>

<script>
import { getMeetingSchedule } from '@/api/mms/meeting'
import { listTracking } from '@/api/mms/tracking'

const CAT_COLORS = {
  '生产': '#16a34a', '研发': '#7c3aed', '业务': '#d97706',
  '周边': '#2563eb', '信息技术': '#0891b2', 'IT': '#0891b2'
}

function pad(n) { return String(n).padStart(2, '0') }
function todayStr() {
  const d = new Date()
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}`
}

export default {
  name: 'Home',
  data() {
    return {
      todayMeetings:  [],
      trackingList:   [],
      todayTotal:     0,
      myTotal:        0,
      openTracking:   0
    }
  },
  computed: {
    userName() { return this.$store.getters.name || '' },
    greeting() {
      const h = new Date().getHours()
      if (h < 6)  return '夜深了'
      if (h < 12) return '早上好'
      if (h < 18) return '下午好'
      return '晚上好'
    },
    dateLabel() {
      const d = new Date()
      const days = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
      return `${d.getFullYear()}年${d.getMonth()+1}月${d.getDate()}日  ${days[d.getDay()]}`
    },
    navItems() {
      return [
        { path: '/mms/dashboard',  title: '会议仪表盘', desc: '今日概况与日历视图',   icon: 'el-icon-data-analysis',    color: '#2563eb', light: '#eff6ff' },
        { path: '/mms/booking',    title: '预约会议',   desc: '向导式快速预约',       icon: 'el-icon-circle-plus-outline', color: '#16a34a', light: '#f0fdf4' },
        { path: '/mms/schedule',   title: '会议排程',   desc: '按日查看全部排程',     icon: 'el-icon-date',              color: '#d97706', light: '#fffbeb' },
        { path: '/mms/meeting',    title: '会议记录',   desc: '历史会议查询与管理',   icon: 'el-icon-document',          color: '#7c3aed', light: '#faf5ff' },
        { path: '/mms/tracking',   title: '跟踪事项',   desc: '会议行动项跟进',       icon: 'el-icon-finished',          color: '#dc2626', light: '#fef2f2' },
        { path: '/mms/room',       title: '会议室管理', desc: '资源配置与维护',       icon: 'el-icon-office-building',   color: '#0891b2', light: '#ecfeff' },
        { path: '/mms/search',     title: '会议查询',   desc: '跨部门全局检索',       icon: 'el-icon-search',            color: '#64748b', light: '#f8fafc' }
      ]
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      const today    = todayStr()
      const username = this.userName

      getMeetingSchedule(today).then(res => {
        const all = res.data || []
        this.todayMeetings = all.sort((a,b) => new Date(a.startTime)-new Date(b.startTime))
        this.todayTotal    = all.length
        this.myTotal       = all.filter(m =>
          m.hostUser === username || m.createBy === username ||
          (m.attendees || []).some(a => a.userName === username)
        ).length
      })

      listTracking({ status: '0', pageNum: 1, pageSize: 10 }).then(res => {
        this.trackingList  = res.rows || []
        this.openTracking  = res.total || 0
      })
    },

    getMeetingState(m) {
      const now = new Date()
      const s   = new Date(m.startTime)
      const e   = new Date(m.endTime)
      if (m.status === '1') return 'cancelled'
      if (now >= s && now < e) return 'ongoing'
      if (now >= e)            return 'done'
      return 'upcoming'
    },

    stateLabel(m) {
      const s = this.getMeetingState(m)
      return { upcoming:'待开始', ongoing:'进行中', done:'已结束', cancelled:'已取消' }[s] || ''
    },

    catColor(cat) { return CAT_COLORS[cat] || '#3b82f6' },

    fmtTime(ts) {
      if (!ts) return ''
      const d = new Date(ts)
      return `${pad(d.getHours())}:${pad(d.getMinutes())}`
    },

    fmtDate(ts) {
      if (!ts) return ''
      const d = new Date(ts)
      return `${d.getMonth()+1}/${d.getDate()}`
    },

    isOverdue(ts) { return ts && new Date(ts) < new Date() }
  }
}
</script>

<style scoped>
.mms-home {
  padding: 20px;
  background: #f1f5f9;
  min-height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

/* ── 欢迎横幅 ── */
.mms-hero {
  position: relative;
  background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
  border-radius: 14px;
  padding: 28px 36px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(37,99,235,.3);
}
.mms-hero-greeting {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 6px;
}
.mms-hero-name { color: #bfdbfe; }
.mms-hero-date {
  font-size: 13px;
  color: rgba(255,255,255,.6);
  margin-bottom: 12px;
}
.mms-hero-summary {
  font-size: 14px;
  color: rgba(255,255,255,.85);
}
.mms-hero-summary b { color: #fff; }
.mms-dot-sep { margin: 0 8px; color: rgba(255,255,255,.4); }
.mms-hero-deco {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255,255,255,.08);
  pointer-events: none;
}
.d1 { width: 300px; height: 300px; right: -60px; top: -80px; background: rgba(255,255,255,.03); }
.d2 { width: 180px; height: 180px; right: 80px;  top: 30px;  background: rgba(255,255,255,.04); }

/* ── 分组标题 ── */
.mms-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
  padding-left: 2px;
}

/* ── 快捷导航 ── */
.mms-nav-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}
@media (max-width: 1400px) { .mms-nav-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 900px)  { .mms-nav-grid { grid-template-columns: repeat(2, 1fr); } }

.mms-nav-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border-radius: 10px;
  padding: 16px 18px;
  border: 1px solid #e5e7eb;
  text-decoration: none;
  transition: box-shadow .15s, transform .15s;
  cursor: pointer;
}
.mms-nav-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,.1);
  transform: translateY(-2px);
}
.mms-nav-icon {
  width: 44px; height: 44px;
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}
.mms-nav-body { flex: 1; min-width: 0; }
.mms-nav-title { font-size: 14px; font-weight: 600; color: #111827; margin-bottom: 3px; }
.mms-nav-desc  { font-size: 12px; color: #9ca3af; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.mms-nav-arrow { color: #d1d5db; font-size: 13px; flex-shrink: 0; }
.mms-nav-card:hover .mms-nav-arrow { color: #6b7280; }

/* ── 今日概览 ── */
.mms-overview-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 16px;
}
@media (max-width: 1200px) { .mms-overview-grid { grid-template-columns: 1fr; } }

/* ── 通用面板 ── */
.mms-panel {
  background: #fff;
  border-radius: 10px;
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
.mms-empty { text-align: center; color: #9ca3af; font-size: 13px; padding: 32px 0; }

/* ── 今日会议列表 ── */
.mms-meeting-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 18px;
  border-bottom: 1px solid #f9fafb;
  transition: background .1s;
}
.mms-meeting-row:last-child { border-bottom: none; }
.mms-meeting-row:hover { background: #fafbff; }
.mms-meeting-row.done    { opacity: .55; }
.mms-meeting-row.cancelled { opacity: .4; }

.mms-meeting-time { width: 46px; flex-shrink: 0; text-align: right; }
.mms-time-start { font-size: 13px; font-weight: 600; color: #374151; }
.mms-time-end   { font-size: 11px; color: #9ca3af; margin-top: 1px; }

.mms-meeting-bar {
  width: 3px; height: 36px; border-radius: 2px; flex-shrink: 0;
}

.mms-meeting-body { flex: 1; min-width: 0; }
.mms-meeting-title {
  font-size: 13px; font-weight: 600; color: #111827;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
  margin-bottom: 4px;
}
.mms-meeting-meta { display: flex; gap: 6px; flex-wrap: wrap; }

.mms-tag-online, .mms-tag-loc, .mms-tag-dept {
  font-size: 11px; padding: 1px 7px; border-radius: 20px; white-space: nowrap;
}
.mms-tag-online { background: #e0f2fe; color: #0369a1; }
.mms-tag-loc    { background: #f3f4f6; color: #4b5563; }
.mms-tag-dept   { background: #eff6ff; color: #1d4ed8; }

.mms-state-tag {
  font-size: 11px; font-weight: 600;
  padding: 2px 8px; border-radius: 20px; flex-shrink: 0; white-space: nowrap;
}
.mms-state-upcoming  { background: #dbeafe; color: #1d4ed8; }
.mms-state-ongoing   { background: #d1fae5; color: #065f46; }
.mms-state-done      { background: #f3f4f6; color: #6b7280; }
.mms-state-cancelled { background: #fee2e2; color: #991b1b; }

.mms-more-hint {
  text-align: center;
  font-size: 12px;
  color: #9ca3af;
  padding: 10px 0 12px;
  border-top: 1px solid #f3f4f6;
}
.mms-more-hint a { color: #2563eb; text-decoration: none; margin-left: 4px; }

/* ── 跟踪事项 ── */
.mms-tracking-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 18px;
  border-bottom: 1px solid #f9fafb;
  transition: background .1s;
}
.mms-tracking-row:last-child { border-bottom: none; }
.mms-tracking-row:hover { background: #fffbeb; }

.mms-tracking-icon {
  width: 30px; height: 30px; border-radius: 8px;
  background: #fef3c7; color: #d97706;
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; flex-shrink: 0; margin-top: 1px;
}
.mms-tracking-body { flex: 1; min-width: 0; }
.mms-tracking-title {
  font-size: 13px; font-weight: 500; color: #111827;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.mms-tracking-meta { font-size: 12px; color: #9ca3af; margin-top: 3px; }
.mms-deadline { color: #6b7280; }
.mms-overdue  { color: #dc2626; font-weight: 600; }

.mms-tracking-from {
  font-size: 11px; color: #9ca3af;
  max-width: 90px; overflow: hidden; text-overflow: ellipsis;
  white-space: nowrap; flex-shrink: 0; margin-top: 2px;
}
</style>
