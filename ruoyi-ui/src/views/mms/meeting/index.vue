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
          <button v-if="m.status==='0'" class="mm-btn mm-btn-danger" @click="handleCancel(m)">取消</button>
          <button v-if="m.status==='0'" class="mm-btn mm-btn-warning" @click="handleComplete(m)">完成</button>
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
import { listMeeting, getMeeting, cancelMeeting, completeMeeting } from '@/api/mms/meeting'

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
      detailData: {}
    }
  },
  created() { this.getList() },
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
    statusLabel(s) { return { '0': '待开始', '1': '已取消', '2': '已完成' }[s] || s }
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
.th-actions { width: 190px; flex-shrink: 0; }

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
</style>
