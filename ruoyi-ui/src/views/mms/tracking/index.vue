<template>
  <div class="app-container">

    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" class="mm-search-bar">
      <el-form-item label="项目议题" prop="project">
        <el-input v-model="queryParams.project" placeholder="请输入项目名称" clearable size="small"
          @keyup.enter.native="handleQuery" style="width:150px" />
      </el-form-item>
      <el-form-item label="跟踪部门" prop="trackDept">
        <el-input v-model="queryParams.trackDept" placeholder="跟踪部门" clearable size="small" style="width:120px" />
      </el-form-item>
      <el-form-item label="发起部门" prop="initDept">
        <el-input v-model="queryParams.initDept" placeholder="发起部门" clearable size="small" style="width:120px" />
      </el-form-item>
      <el-form-item label="是否结案" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable size="small" style="width:100px">
          <el-option label="跟进中" value="0" />
          <el-option label="已结案" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <div class="mm-toolbar">
      <div style="display:flex;align-items:center;gap:14px">
        <span class="mm-list-title">会议跟踪事项</span>
        <span class="mm-stat-group">
          <span class="mm-stat">跟进中 <b class="mm-stat-n mm-stat-orange">{{ inProgressCount }}</b></span>
          <span class="mm-stat-sep">|</span>
          <span class="mm-stat">已结案 <b class="mm-stat-n">{{ closedCount }}</b></span>
        </span>
      </div>
      <div style="display:flex;gap:8px;align-items:center">
        <el-button size="mini" :icon="showSearch?'el-icon-arrow-up':'el-icon-search'"
          @click="showSearch=!showSearch" plain>筛选</el-button>
        <el-button type="primary" size="small" icon="el-icon-plus"
          @click="handleAdd" v-hasPermi="['mms:tracking:add']">新增</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="mm-table" v-loading="loading">
      <div class="mm-thead">
        <div class="mm-th th-seq">序</div>
        <div class="mm-th th-project">项目/议题</div>
        <div class="mm-th th-report">部门汇报</div>
        <div class="mm-th th-director">主管指示</div>
        <div class="mm-th th-progress">处理进度</div>
        <div class="mm-th th-depts">主导 / 部门</div>
        <div class="mm-th th-status">状态</div>
        <div class="mm-th th-dates">任务 / 计划</div>
        <div class="mm-th th-actions">操作</div>
      </div>

      <div v-for="row in trackingList" :key="row.trackingId" class="mm-row">
        <div class="mm-td th-seq mm-seq">{{ row.seqNo }}</div>

        <div class="mm-td th-project">
          <span class="mm-project-link" @click="showDetail(row)">{{ row.project }}</span>
          <div v-if="row.meetingTitle" class="mm-meeting-sub">{{ row.meetingTitle }}</div>
        </div>

        <div class="mm-td th-report mm-report">{{ row.deptReport || '—' }}</div>

        <div class="mm-td th-director mm-director">{{ row.directorNote || '—' }}</div>

        <div class="mm-td th-progress">
          <div v-if="row.progressList && row.progressList.length" class="mm-progress-list">
            <div v-for="(p, i) in row.progressList" :key="i" class="mm-progress-line">
              <span class="mm-prog-date">{{ p.progressDate }}：</span>{{ p.content }}
            </div>
          </div>
          <span v-else class="mm-empty-val">—</span>
        </div>

        <div class="mm-td th-depts">
          <div class="mm-dept-lead">{{ row.leadDept || '—' }}</div>
          <div class="mm-dept-sub" v-if="row.trackDept">跟：{{ row.trackDept }}</div>
          <div class="mm-dept-sub" v-if="row.initDept">发：{{ row.initDept }}</div>
        </div>

        <div class="mm-td th-status">
          <span :class="['mm-status-tag', row.status==='1'?'st-closed':'st-open']">
            {{ row.status==='1' ? '已结案' : '跟进中' }}
          </span>
        </div>

        <div class="mm-td th-dates">
          <div class="mm-date-row"><span class="mm-date-label">任务</span>{{ formatDate(row.taskDate) }}</div>
          <div class="mm-date-row"><span class="mm-date-label">计划</span>{{ formatDate(row.plannedDate) }}</div>
        </div>

        <div class="mm-td th-actions">
          <button class="mm-btn mm-btn-default" @click="showDetail(row)">详情</button>
          <button class="mm-btn mm-btn-primary" @click="openProgress(row)">更新进度</button>
          <button v-if="row.status==='0'" class="mm-btn mm-btn-success" @click="handleClose(row)">结案</button>
        </div>
      </div>

      <div v-if="!trackingList.length && !loading" class="mm-empty">暂无跟踪事项</div>
    </div>

    <pagination v-show="total>0" :total="total"
      :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" style="margin-top:12px" />

    <!-- 详情弹窗 -->
    <el-dialog :title="'跟踪事项详情 — ' + detailData.project" :visible.sync="detailVisible" width="680px" append-to-body>

      <!-- 项目信息栏 -->
      <div class="pd-info-bar">
        <div class="pd-info-project">
          项目：{{ detailData.project }}
          <span v-if="detailData.meetingTitle">（{{ detailData.meetingTitle }}）</span>
        </div>
        <div class="pd-info-dept">
          <span v-if="detailData.trackDept">跟踪部门：{{ detailData.trackDept }}</span>
          <span v-if="detailData.trackDept && detailData.initDept"> · </span>
          <span v-if="detailData.initDept">发起部门：{{ detailData.initDept }}</span>
          <span v-if="detailData.leadDept"> · 主导：{{ detailData.leadDept }}</span>
        </div>
      </div>

      <!-- 元信息行 -->
      <div class="pd-meta-row">
        <div class="pd-meta-item">
          <span class="pd-meta-label">状态</span>
          <span :class="['mm-status-tag', detailData.status==='1'?'st-closed':'st-open']">
            {{ detailData.status==='1' ? '已结案' : '跟进中' }}
          </span>
        </div>
        <div class="pd-meta-item">
          <span class="pd-meta-label">任务日期</span>
          <span class="pd-meta-val">{{ formatDate(detailData.taskDate) }}</span>
        </div>
        <div class="pd-meta-item">
          <span class="pd-meta-label">计划完成</span>
          <span class="pd-meta-val">{{ formatDate(detailData.plannedDate) }}</span>
        </div>
      </div>

      <!-- 部门汇报 -->
      <div class="pd-section">
        <div class="pd-section-title">部门汇报</div>
        <div class="pd-section-body">{{ detailData.deptReport || '—' }}</div>
      </div>

      <!-- 主管指示 -->
      <div class="pd-section">
        <div class="pd-section-title">主管指示</div>
        <div class="pd-section-body pd-section-amber">{{ detailData.directorNote || '—' }}</div>
      </div>

      <!-- 处理进度 -->
      <div class="pd-history" style="margin-top:12px">
        <div class="pd-history-title">处理进度</div>
        <template v-if="detailData.progressList && detailData.progressList.length">
          <div v-for="(p, idx) in detailData.progressList" :key="idx" class="pd-history-item">
            <span class="pd-hist-date">{{ p.progressDate }}：</span>{{ p.content }}
          </div>
        </template>
        <div v-else class="trk-empty-note">暂无进度记录</div>
      </div>

      <div slot="footer">
        <el-button @click="detailVisible=false">关闭</el-button>
        <el-button type="primary" @click="openProgress(detailData)">更新进度</el-button>
        <el-button v-if="detailData.status==='0'" type="success" @click="handleClose(detailData)">结案</el-button>
      </div>
    </el-dialog>

    <!-- 新增跟踪事项 -->
    <el-dialog title="新增跟踪事项" :visible.sync="addVisible" width="640px" append-to-body>
      <el-form ref="addForm" :model="addForm" :rules="addRules" label-width="90px">
        <el-row :gutter="14">
          <el-col :span="8">
            <el-form-item label="序号" prop="seqNo">
              <el-input-number v-model="addForm.seqNo" :min="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item label="项目/议题" prop="project">
              <el-input v-model="addForm.project" placeholder="请输入项目/议题名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="关联会议">
          <el-select v-model="addForm.meetingId" placeholder="请选择关联会议（可选）" filterable clearable
            style="width:100%" :loading="meetingLoading" @change="onMeetingSelect" @clear="onMeetingClear">
            <el-option v-for="m in myMeetings" :key="m.meetingId" :value="m.meetingId" :label="m.title">
              <div style="display:flex;justify-content:space-between;align-items:center;gap:16px">
                <span style="font-weight:500">{{ m.title }}</span>
                <span style="font-size:11px;color:#9ca3af;white-space:nowrap">{{ fmtMeetingTime(m.startTime) }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-row :gutter="14">
          <el-col :span="8">
            <el-form-item label="跟踪部门">
              <el-input v-model="addForm.trackDept" placeholder="跟踪部门" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="发起部门">
              <el-input v-model="addForm.initDept" placeholder="发起部门" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="主导部门">
              <el-input v-model="addForm.leadDept" placeholder="主导部门" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="任务日期">
              <el-date-picker v-model="addForm.taskDate" type="date" value-format="yyyy-MM-dd" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划完成">
              <el-date-picker v-model="addForm.plannedDate" type="date" value-format="yyyy-MM-dd" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="部门汇报">
          <el-input v-model="addForm.deptReport" type="textarea" :rows="3" placeholder="部门汇报内容" />
        </el-form-item>
        <el-form-item label="主管指示">
          <el-input v-model="addForm.directorNote" type="textarea" :rows="3" placeholder="主管指示内容" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitAdd">确 定</el-button>
        <el-button @click="addVisible=false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 更新进度 -->
    <el-dialog :title="'更新跟踪进度 — ' + progressTracking.project" :visible.sync="progressVisible" width="620px" append-to-body>

      <!-- 项目信息 -->
      <div class="pd-info-bar">
        <div class="pd-info-project">
          项目：{{ progressTracking.project }}
          <span v-if="progressTracking.meetingTitle">（{{ progressTracking.meetingTitle }}）</span>
        </div>
        <div class="pd-info-dept">
          <span v-if="progressTracking.trackDept">跟踪部门：{{ progressTracking.trackDept }}</span>
          <span v-if="progressTracking.trackDept && progressTracking.initDept"> · </span>
          <span v-if="progressTracking.initDept">发起部门：{{ progressTracking.initDept }}</span>
        </div>
      </div>

      <!-- 历史进度 -->
      <div v-if="progressTracking.progressList && progressTracking.progressList.length" class="pd-history">
        <div class="pd-history-title">历史进度</div>
        <div v-for="(p, i) in progressTracking.progressList" :key="i" class="pd-history-item">
          <span class="pd-hist-date">{{ p.progressDate }}：</span>{{ p.content }}
        </div>
      </div>

      <!-- 分隔 -->
      <div class="pd-divider"></div>

      <!-- 表单 -->
      <el-form ref="progressForm" :model="progressForm" label-position="top" class="pd-form">
        <el-row :gutter="14">
          <el-col :span="8">
            <el-form-item label="进度日期" prop="progressDate"
              :rules="[{required:true,message:'请输入进度日期',trigger:'blur'}]">
              <el-input v-model="progressForm.progressDate" placeholder="如：4/21" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="计划完成" prop="plannedDate">
              <el-date-picker v-model="progressForm.plannedDate" type="date"
                value-format="yyyy-MM-dd" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="标记状态" prop="status">
              <el-select v-model="progressForm.status" style="width:100%">
                <el-option label="跟进中" value="0" />
                <el-option label="已结案" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="进度说明" prop="content"
          :rules="[{required:true,message:'请输入进度内容',trigger:'blur'}]">
          <el-input v-model="progressForm.content" type="textarea" :rows="4"
            placeholder="请详细描述本次处理进展…" />
        </el-form-item>
      </el-form>

      <div slot="footer">
        <el-button @click="progressVisible=false">关闭</el-button>
        <el-button type="primary" @click="submitProgress">提交更新</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listTracking, getTracking, addTracking, delTracking, addProgress, closeTracking } from '@/api/mms/tracking'
import { listMeeting } from '@/api/mms/meeting'

export default {
  name: 'MmsTracking',
  data() {
    return {
      loading: true,
      showSearch: false,
      ids: [],
      selectedIds: [],
      single: true,
      multiple: true,
      total: 0,
      trackingList: [],
      queryParams: { pageNum: 1, pageSize: 10, project: undefined, trackDept: undefined, initDept: undefined, status: undefined },
      detailVisible: false,
      detailData: {},
      addVisible: false,
      addForm: {},
      addRules: { project: [{ required: true, message: '项目/议题不能为空', trigger: 'blur' }] },
      myMeetings: [],
      meetingLoading: false,
      progressVisible: false,
      progressTracking: {},
      progressForm: { trackingId: null, progressDate: '', content: '' }
    }
  },
  computed: {
    inProgressCount() { return this.trackingList.filter(r => r.status !== '1').length },
    closedCount() { return this.trackingList.filter(r => r.status === '1').length }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.loading = true
      listTracking(this.queryParams).then(res => {
        this.trackingList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    showDetail(row) {
      getTracking(row.trackingId).then(res => {
        this.detailData = res.data
        this.detailVisible = true
      })
    },
    handleAdd() {
      this.addForm = { seqNo: this.trackingList.length + 1, project: '', meetingId: undefined, meetingTitle: '', trackDept: '', initDept: '', leadDept: '', deptReport: '', directorNote: '', taskDate: '', plannedDate: '', status: '0' }
      this.addVisible = true
      this.loadMyMeetings()
    },
    loadMyMeetings() {
      this.meetingLoading = true
      listMeeting({ pageNum: 1, pageSize: 200 }).then(res => {
        this.myMeetings = (res.rows || []).filter(m => m.status !== '1')
        this.meetingLoading = false
      }).catch(() => { this.meetingLoading = false })
    },
    onMeetingSelect(meetingId) {
      const m = this.myMeetings.find(x => x.meetingId === meetingId)
      this.addForm.meetingTitle = m ? m.title : ''
    },
    onMeetingClear() {
      this.addForm.meetingId = undefined
      this.addForm.meetingTitle = ''
    },
    fmtMeetingTime(ts) {
      if (!ts) return ''
      const d = new Date(ts)
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
    },
    submitAdd() {
      this.$refs.addForm.validate(valid => {
        if (!valid) return
        addTracking(this.addForm).then(() => {
          this.msgSuccess('新增成功')
          this.addVisible = false
          this.getList()
        })
      })
    },
    openProgress(row) {
      this.detailVisible = false
      this.progressTracking = row
      const now = new Date()
      const todayLabel = `${now.getMonth() + 1}/${now.getDate()}`
      this.progressForm = {
        trackingId: row.trackingId,
        progressDate: todayLabel,
        content: '',
        plannedDate: row.plannedDate ? this.formatDate(row.plannedDate) : '',
        status: row.status || '0'
      }
      this.progressVisible = true
    },
    submitProgress() {
      this.$refs.progressForm.validate(valid => {
        if (!valid) return
        const { status, plannedDate, ...rest } = this.progressForm
        const tasks = [addProgress(rest)]
        if (status === '1' && this.progressTracking.status !== '1') {
          tasks.push(closeTracking(this.progressTracking.trackingId))
        }
        Promise.all(tasks).then(() => {
          this.msgSuccess('进度已更新')
          this.progressVisible = false
          this.getList()
        })
      })
    },
    handleClose(row) {
      this.$confirm(`确定将"${row.project}"结案吗？结案后不再跟踪。`, '提示', { type: 'warning' }).then(() => {
        return closeTracking(row.trackingId)
      }).then(() => {
        this.msgSuccess('已结案')
        this.detailVisible = false
        this.getList()
      })
    },
    handleDelete(row) {
      const ids = row && row.trackingId ? row.trackingId : this.ids
      this.$confirm('是否确认删除该跟踪事项？', '警告', { type: 'warning' })
        .then(() => delTracking(ids))
        .then(() => { this.getList(); this.msgSuccess('删除成功') })
    },
    formatDate(ts) {
      if (!ts) return '—'
      const d = new Date(ts)
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
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
.mm-stat-group { display: flex; align-items: center; gap: 6px; }
.mm-stat { font-size: 13px; color: #6b7280; }
.mm-stat-n { font-weight: 700; color: #374151; }
.mm-stat-orange { color: #d97706; }
.mm-stat-sep { color: #d1d5db; }

/* ── 表格容器 ── */
.mm-table {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
}

/* ── 列宽（固定 + flex，无横向滚动） ── */
.th-seq      { width: 44px;  flex-shrink: 0; }
.th-project  { width: 120px; flex-shrink: 0; }
.th-report   { flex: 1.5; min-width: 110px; }
.th-director { flex: 1.3; min-width: 100px; }
.th-progress { flex: 1.8; min-width: 130px; }
.th-depts    { width: 96px;  flex-shrink: 0; }
.th-status   { width: 72px;  flex-shrink: 0; }
.th-dates    { width: 104px; flex-shrink: 0; }
.th-actions  { width: 152px; flex-shrink: 0; }

/* ── 表头 ── */
.mm-thead {
  display: flex; align-items: center;
  background: #f9fafb; padding: 10px 16px;
  border-bottom: 1px solid #e5e7eb;
}
.mm-th { font-size: 12px; font-weight: 600; color: #6b7280; }

/* ── 数据行 ── */
.mm-row {
  display: flex; align-items: flex-start;
  padding: 12px 16px;
  border-bottom: 1px solid #f3f4f6;
  transition: background .12s;
}
.mm-row:last-child { border-bottom: none; }
.mm-row:hover { background: #f8faff; }

.mm-td { font-size: 13px; color: #374151; padding-right: 8px; padding-top: 1px; }

.mm-seq { font-size: 12px; color: #9ca3af; font-weight: 600; text-align: center; padding-top: 2px; }

.mm-project-link {
  color: #2563eb; cursor: pointer; font-weight: 500; font-size: 13px;
  line-height: 1.5; word-break: break-all;
}
.mm-project-link:hover { text-decoration: underline; }

.mm-meeting-sub { font-size: 11px; color: #9ca3af; margin-top: 2px; line-height: 1.4; word-break: break-all; }

.mm-report   { font-size: 12px; color: #374151; line-height: 1.6; white-space: pre-wrap; word-break: break-all; }
.mm-director { font-size: 12px; color: #d97706; line-height: 1.6; white-space: pre-wrap; word-break: break-all; }

.mm-progress-list { display: flex; flex-direction: column; gap: 3px; }
.mm-progress-line { font-size: 12px; color: #1d4ed8; line-height: 1.6; word-break: break-all; }
.mm-prog-date { font-weight: 600; white-space: nowrap; }
.mm-empty-val { color: #d1d5db; font-size: 13px; }

.mm-dept-lead { font-size: 12px; font-weight: 600; color: #374151; }
.mm-dept-sub  { font-size: 11px; color: #9ca3af; line-height: 1.5; }

.mm-date-row  { font-size: 12px; color: #374151; line-height: 1.7; }
.mm-date-label { display: inline-block; width: 28px; font-size: 11px; color: #9ca3af; }

/* ── 状态标签 ── */
.mm-status-tag {
  display: inline-block; font-size: 11px; padding: 2px 10px;
  border-radius: 20px; font-weight: 600; white-space: nowrap;
}
.st-open   { background: #fef3c7; color: #d97706; border: 1px solid #fcd34d; }
.st-closed { background: #f3f4f6; color: #6b7280; border: 1px solid #e5e7eb; }

/* ── 操作按钮 ── */
.mm-td.th-actions { display: flex; gap: 4px; align-items: flex-start; flex-wrap: nowrap; }
.mm-btn {
  display: inline-flex; align-items: center;
  font-size: 12px; padding: 4px 10px; border-radius: 5px;
  border: none; cursor: pointer; font-weight: 500; line-height: 1.5;
  white-space: nowrap; transition: opacity .15s;
}
.mm-btn:hover { opacity: .82; }
.mm-btn-default { background: #f3f4f6; color: #374151; border: 1px solid #e5e7eb; }
.mm-btn-primary { background: #0891b2; color: #fff; }
.mm-btn-success { background: #16a34a; color: #fff; }

.mm-empty { text-align: center; color: #9ca3af; padding: 48px 0; font-size: 13px; }

/* ── 详情弹窗 ── */
.trk-section-title { font-weight: 600; color: #374151; margin-bottom: 6px; font-size: 13px; }
.trk-text-block {
  background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 6px;
  padding: 10px 14px; font-size: 13px; color: #4b5563; white-space: pre-wrap;
  line-height: 1.6; min-height: 36px;
}
.trk-text-amber { background: #fffbeb; border-color: #fcd34d; color: #d97706; }
.trk-progress-detail { display: flex; flex-direction: column; }
.trk-prog-detail-item {
  display: flex; gap: 12px; padding: 8px 0; border-bottom: 1px solid #f3f4f6;
}
.trk-prog-detail-item:last-child { border-bottom: none; }
.trk-prog-detail-date { font-size: 12px; color: #9ca3af; white-space: nowrap; min-width: 50px; }
.trk-prog-detail-content { font-size: 13px; color: #374151; white-space: pre-wrap; line-height: 1.6; }
.trk-empty-note { color: #9ca3af; font-size: 13px; padding: 12px 0; text-align: center; }

/* ── 详情 & 进度弹窗共用 ── */
.pd-meta-row {
  display: flex; align-items: center; gap: 24px;
  padding: 10px 0 14px; border-bottom: 1px solid #f3f4f6; margin-bottom: 14px;
}
.pd-meta-item { display: flex; align-items: center; gap: 6px; }
.pd-meta-label { font-size: 12px; color: #9ca3af; }
.pd-meta-val { font-size: 13px; color: #374151; font-weight: 500; }

.pd-section { margin-bottom: 12px; }
.pd-section-title { font-size: 12px; font-weight: 600; color: #6b7280; margin-bottom: 6px; }
.pd-section-body {
  background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 6px;
  padding: 10px 14px; font-size: 13px; color: #374151;
  white-space: pre-wrap; line-height: 1.6; min-height: 36px;
}
.pd-section-amber { background: #fffbeb; border-color: #fcd34d; color: #d97706; }

/* ── 更新进度弹窗 ── */
.pd-info-bar {
  background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 6px;
  padding: 10px 14px; margin-bottom: 14px;
}
.pd-info-project { font-size: 14px; font-weight: 600; color: #1f2937; margin-bottom: 4px; }
.pd-info-dept { font-size: 12px; color: #6b7280; }

.pd-history {
  background: #f8faff; border: 1px solid #dbeafe; border-radius: 6px; padding: 10px 14px;
}
.pd-history-title { font-size: 12px; font-weight: 600; color: #6b7280; margin-bottom: 8px; }
.pd-history-item { font-size: 13px; color: #1d4ed8; line-height: 1.7; }
.pd-hist-date { font-weight: 700; }
.pd-divider { height: 1px; background: #e5e7eb; margin: 16px 0; }
.pd-form >>> .el-form-item__label { white-space: nowrap; }
</style>
