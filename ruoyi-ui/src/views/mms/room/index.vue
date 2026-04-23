<template>
  <div class="app-container">
    <!-- 页头工具栏 -->
    <div class="room-toolbar">
      <div style="display:flex;align-items:center;gap:8px;flex-wrap:wrap">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable size="small" style="width:110px" @change="getList">
          <el-option label="正常" value="0" />
          <el-option label="禁用" value="1" />
        </el-select>
        <el-button size="small" icon="el-icon-refresh" @click="getList">刷新</el-button>
      </div>
      <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-hasPermi="['mms:room:add']">新增会议室</el-button>
    </div>

    <!-- 园区标签页 -->
    <el-tabs v-model="activeCampus" @tab-click="handleCampusTab" style="margin-bottom:16px">
      <el-tab-pane label="全部" name=""></el-tab-pane>
      <el-tab-pane v-for="c in campuses" :key="c" :label="c" :name="c"></el-tab-pane>
    </el-tabs>

    <!-- 卡片网格 -->
    <div v-loading="loading" class="room-grid">
      <div
        v-for="room in roomList"
        :key="room.roomId"
        :class="['room-card', 'rc-' + statusDotClass(room)]"
        @click="handleCardClick(room)">

        <!-- 顶部：名称 + 状态胶囊 -->
        <div class="rc-header">
          <div class="rc-name">{{ room.roomName || room.roomNumber }}</div>
          <span :class="['rc-status', statusDotClass(room)]">
            <i class="rc-dot"></i>{{ statusText(room) }}
          </span>
        </div>

        <!-- 园区 · 房间号 -->
        <div class="rc-sub">{{ room.campus }} · {{ room.roomNumber }}</div>

        <div class="rc-divider"></div>

        <!-- 容量 -->
        <div class="rc-meta">
          <span class="rc-cap"><i class="el-icon-user"></i> {{ room.capacity }} 人</span>
          <span v-if="room.isAbnormal === '1'" class="rc-abnormal"><i class="el-icon-warning"></i> 设备异常</span>
        </div>

        <!-- 设备 -->
        <div class="rc-equip">
          <span
            v-for="eq in splitEq(room.equipment)" :key="eq"
            :class="['rc-eq-tag', eqClass(eq), isDamaged(room, eq) ? 'is-damaged' : '']">
            {{ eq }}
          </span>
          <span v-if="!room.equipment" class="rc-no-eq">暂无设备</span>
        </div>
      </div>

      <div v-if="!loading && roomList.length === 0" class="room-empty">
        暂无会议室数据
      </div>
    </div>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- ── 会议室详情弹窗 ── -->
    <el-dialog :title="detail.campus + detail.roomNumber + ' — 会议室详情'" :visible.sync="detailVisible" width="700px" append-to-body>
      <el-descriptions :column="2" border size="small" style="margin-bottom:20px">
        <el-descriptions-item label="园区">{{ detail.campus }}</el-descriptions-item>
        <el-descriptions-item label="编号">{{ detail.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="容量">{{ detail.capacity }} 人</el-descriptions-item>
        <el-descriptions-item label="设备">
          <template v-if="detail.equipment">
            <el-tag v-for="eq in splitEq(detail.equipment)" :key="eq" size="mini"
              :type="isDamaged(detail, eq) ? 'danger' : ''" style="margin:2px">{{ eq }}</el-tag>
          </template>
          <span v-else>—</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag size="mini" :type="detail.status === '0' ? 'success' : 'info'">
            {{ detail.status === '0' ? '正常' : '禁用' }}
          </el-tag>
          <el-tag v-if="detail.isAbnormal === '1'" size="mini" type="danger" style="margin-left:6px">设备异常</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.remark || '—' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 操作按钮 -->
      <div style="display:flex;gap:8px;margin-bottom:16px;flex-wrap:wrap">
        <el-button size="small" icon="el-icon-edit" @click="handleUpdate(detail)" v-hasPermi="['mms:room:edit']">修改信息</el-button>
        <el-button size="small" icon="el-icon-delete" type="danger" plain @click="handleDelete(detail)" v-hasPermi="['mms:room:remove']">删除</el-button>
        <el-button size="small" icon="el-icon-search" type="primary" plain @click="openInspect(null)">日常点检</el-button>
        <el-button v-if="detail.isAbnormal === '1'" size="small" icon="el-icon-circle-check" type="success" plain @click="clearAbnormal">解除异常</el-button>
      </div>

      <!-- 历史使用记录 -->
      <div class="detail-section-title">历史使用记录（最近20条）</div>
      <el-table :data="roomMeetings" size="small" v-loading="meetingLoading" style="width:100%">
        <el-table-column label="日期时间" width="170">
          <template slot-scope="s">
            {{ fmtRange(s.row.startTime, s.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="会议主题" prop="title" show-overflow-tooltip />
        <el-table-column label="类型" width="70">
          <template slot-scope="s">
            <span :class="['mms-cat-badge', 'cat-' + s.row.category]">{{ s.row.category }}</span>
          </template>
        </el-table-column>
        <el-table-column label="主持人" prop="hostName" width="100" />
        <el-table-column label="状态" width="80">
          <template slot-scope="s">
            <el-tag size="mini" :type="meetingStatusType(s.row.status)">{{ meetingStatusLabel(s.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90" align="center">
          <template slot-scope="s">
            <el-button v-if="s.row.status === '2'" size="mini" type="text" icon="el-icon-finished"
              @click="openInspect(s.row)" v-hasPermi="['mms:room:edit']">交接确认</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- ── 新增/修改对话框 ── -->
    <el-dialog :title="formTitle" :visible.sync="formOpen" width="560px" append-to-body>
      <el-form ref="roomForm" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="园区" prop="campus">
              <el-select v-model="form.campus" placeholder="请选择园区" style="width:100%">
                <el-option v-for="c in campuses" :key="c" :label="c" :value="c" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber">
              <el-input v-model="form.roomNumber" placeholder="如：101" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="会议室名称" prop="roomName">
              <el-input v-model="form.roomName" placeholder="请输入会议室名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="容纳人数" prop="capacity">
              <el-input-number v-model="form.capacity" :min="1" :max="500" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="设备">
              <el-select v-model="equipmentArr" multiple filterable allow-create placeholder="选择或输入设备" style="width:100%"
                @change="syncEquipment">
                <el-option v-for="eq in equipmentPool" :key="eq" :label="eq" :value="eq" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="可选备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="formOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- ── 点检/交接确认弹窗 ── -->
    <el-dialog
      :title="inspectForm.inspectType === '0' ? '交接确认 — ' + (inspectMeeting ? inspectMeeting.title : '') : '日常点检 — ' + detail.campus + detail.roomNumber"
      :visible.sync="inspectOpen"
      width="480px"
      append-to-body>

      <el-form :model="inspectForm" label-width="80px">
        <el-form-item label="检查结果">
          <el-radio-group v-model="inspectForm.result">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">有设备损坏</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="inspectForm.result === '1'" label="损坏设备">
          <el-checkbox-group v-model="damagedArr">
            <el-checkbox v-for="eq in splitEq(detail.equipment)" :key="eq" :label="eq">{{ eq }}</el-checkbox>
          </el-checkbox-group>
          <div style="margin-top:8px">
            <el-input v-model="customDamaged" size="small" placeholder="其他损坏设备（逗号分隔）" clearable />
          </div>
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="inspectForm.note" type="textarea" :rows="2" placeholder="可填写说明或问题描述" />
        </el-form-item>

        <el-alert v-if="inspectForm.result === '1'" type="warning" :closable="false" show-icon
          title="确认后会议室将标记为【设备异常】，无法接受新预约，请及时安排维修并解除异常状态。" />
      </el-form>

      <div slot="footer">
        <el-button type="primary" :loading="inspectSaving" @click="submitInspect">提 交</el-button>
        <el-button @click="inspectOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRoom, getRoom, getRoomMeetings, addRoom, updateRoom, delRoom, submitInspection } from '@/api/mms/room'

export default {
  name: 'MmsRoom',
  data() {
    return {
      loading: true,
      total: 0,
      roomList: [],
      activeCampus: '',
      campuses: ['1园', '2园', '3园'],
      equipmentPool: ['投影仪', '视频会议系统', '白板', '电话会议', '智能大屏', '音响系统', '摄像头', '打印机'],
      queryParams: { pageNum: 1, pageSize: 20, campus: undefined, status: undefined },

      // 详情弹窗
      detailVisible: false,
      detail: {},
      roomMeetings: [],
      meetingLoading: false,

      // 新增/修改
      formOpen: false,
      formTitle: '',
      form: {},
      equipmentArr: [],
      rules: {
        campus: [{ required: true, message: '请选择园区', trigger: 'change' }],
        roomNumber: [{ required: true, message: '房间号不能为空', trigger: 'blur' }]
      },

      // 点检/交接
      inspectOpen: false,
      inspectMeeting: null,
      inspectForm: { roomId: null, meetingId: null, inspectType: '1', result: '0', damagedEquip: '', note: '' },
      damagedArr: [],
      customDamaged: '',
      inspectSaving: false
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.loading = true
      listRoom(this.queryParams).then(res => {
        this.roomList = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    handleCampusTab(tab) {
      this.queryParams.campus = tab.name || undefined
      this.queryParams.pageNum = 1
      this.getList()
    },
    splitEq(str) {
      return str ? str.split(',').filter(Boolean) : []
    },
    syncEquipment(arr) {
      this.form.equipment = arr.join(',')
    },
    statusDotClass(room) {
      if (room.isAbnormal === '1') return 'dot-abnormal'
      if (room.status === '1') return 'dot-disabled'
      return room.occupied ? 'dot-occupied' : 'dot-free'
    },
    statusIcon(room) {
      if (room.isAbnormal === '1') return 'el-icon-warning'
      if (room.status === '1') return 'el-icon-minus-circle'
      return room.occupied ? 'el-icon-user' : 'el-icon-circle-check'
    },
    statusText(room) {
      if (room.isAbnormal === '1') return '设备异常'
      if (room.status === '1') return '已禁用'
      return room.occupied ? '使用中' : '空闲'
    },
    eqClass(eq) {
      const s = eq.toLowerCase()
      if (s.includes('投影') || s.includes('大屏') || s.includes('显示')) return 'eq-display'
      if (s.includes('视频') || s.includes('摄像')) return 'eq-video'
      if (s.includes('音响') || s.includes('麦克') || s.includes('电话')) return 'eq-audio'
      if (s.includes('白板') || s.includes('黑板')) return 'eq-board'
      if (s.includes('网络') || s.includes('wifi')) return 'eq-net'
      return 'eq-default'
    },
    isDamaged(room, eq) {
      if (!room.damagedEquipment) return false
      return room.damagedEquipment.split(',').includes(eq)
    },
    meetingStatusLabel(s) {
      return { '0': '已确认', '1': '已取消', '2': '已完成' }[s] || s
    },
    meetingStatusType(s) {
      return { '0': 'primary', '1': 'info', '2': 'success' }[s] || ''
    },
    fmtRange(start, end) {
      if (!start) return ''
      const fmt = ts => {
        const d = new Date(ts)
        return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
      }
      const es = new Date(end)
      return `${fmt(start)}–${String(es.getHours()).padStart(2,'0')}:${String(es.getMinutes()).padStart(2,'0')}`
    },

    // ── 卡片点击展示详情 ──
    handleCardClick(room) {
      this.detail = { ...room }
      this.detailVisible = true
      this.roomMeetings = []
      this.meetingLoading = true
      getRoomMeetings(room.roomId).then(res => {
        this.roomMeetings = res.data || []
        this.meetingLoading = false
      }).catch(() => { this.meetingLoading = false })
    },

    // ── 新增/修改 ──
    handleAdd() {
      this.resetForm()
      this.formOpen = true
      this.formTitle = '新增会议室'
    },
    handleUpdate(row) {
      this.resetForm()
      const id = row.roomId
      getRoom(id).then(res => {
        this.form = res.data
        this.equipmentArr = this.splitEq(res.data.equipment)
        this.formOpen = true
        this.formTitle = '修改会议室'
      })
    },
    submitForm() {
      this.$refs['roomForm'].validate(valid => {
        if (!valid) return
        if (this.form.roomId) {
          updateRoom(this.form).then(() => {
            this.msgSuccess('修改成功')
            this.formOpen = false
            this.detailVisible = false
            this.getList()
          })
        } else {
          addRoom(this.form).then(() => {
            this.msgSuccess('新增成功')
            this.formOpen = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm(`是否确认删除会议室"${row.campus}${row.roomNumber}"？`, '警告', { type: 'warning' })
        .then(() => delRoom(row.roomId))
        .then(() => {
          this.getList()
          this.detailVisible = false
          this.msgSuccess('删除成功')
        })
    },
    resetForm() {
      this.form = { roomId: undefined, campus: undefined, roomNumber: undefined, roomName: '', capacity: 10, equipment: '', status: '0', remark: '' }
      this.equipmentArr = []
      this.$nextTick(() => { this.$refs['roomForm'] && this.$refs['roomForm'].resetFields() })
    },

    // ── 解除异常 ──
    clearAbnormal() {
      this.$confirm(`确认解除"${this.detail.campus}${this.detail.roomNumber}"的异常状态？`, '提示', { type: 'warning' })
        .then(() => {
          const payload = { ...this.detail, inspectType: '1', result: '0', damagedEquip: '', note: '手动解除异常', roomId: this.detail.roomId }
          return submitInspection(payload)
        })
        .then(() => {
          this.msgSuccess('已解除异常状态')
          this.detail.isAbnormal = '0'
          this.detail.damagedEquipment = ''
          this.getList()
        })
    },

    // ── 点检/交接 ──
    openInspect(meeting) {
      this.inspectMeeting = meeting
      this.damagedArr = []
      this.customDamaged = ''
      this.inspectForm = {
        roomId: this.detail.roomId,
        meetingId: meeting ? meeting.meetingId : null,
        inspectType: meeting ? '0' : '1',
        result: '0',
        damagedEquip: '',
        note: ''
      }
      this.inspectOpen = true
    },
    submitInspect() {
      // 合并损坏设备
      let damaged = [...this.damagedArr]
      if (this.customDamaged.trim()) {
        damaged = damaged.concat(this.customDamaged.trim().split(',').map(s => s.trim()).filter(Boolean))
      }
      this.inspectForm.damagedEquip = damaged.join(',')
      this.inspectSaving = true
      submitInspection(this.inspectForm).then(() => {
        this.inspectSaving = false
        this.inspectOpen = false
        const abnormal = this.inspectForm.result === '1'
        this.msgSuccess(abnormal ? '已记录异常，会议室已标记为不可预约' : '点检完成，状态正常')
        // 同步 detail
        this.detail.isAbnormal = this.inspectForm.result
        this.detail.damagedEquipment = this.inspectForm.damagedEquip
        this.getList()
      }).catch(() => { this.inspectSaving = false })
    }
  }
}
</script>

<style scoped lang="scss">
.room-toolbar {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 16px; flex-wrap: wrap; gap: 8px;
}

/* ── 卡片网格 ── */
.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
  gap: 14px;
  margin-bottom: 20px;
}

.room-card {
  background: #fff;
  border: 1px solid #e8eef8;
  border-top: 3px solid #e8eef8;
  border-radius: 10px;
  padding: 14px 16px 12px;
  cursor: pointer;
  transition: box-shadow .2s, transform .15s;
  &:hover { box-shadow: 0 6px 20px rgba(26,86,219,.1); transform: translateY(-2px); }
  &.rc-dot-free     { border-top-color: #22c55e; }
  &.rc-dot-occupied { border-top-color: #ef4444; }
  &.rc-dot-disabled { border-top-color: #9ca3af; }
  &.rc-dot-abnormal { border-top-color: #f59e0b; background: #fffbf0; }
}

/* 名称行 */
.rc-header {
  display: flex; justify-content: space-between; align-items: flex-start; gap: 6px;
  margin-bottom: 3px;
}
.rc-name {
  font-size: 14px; font-weight: 700; color: #111827;
  line-height: 1.4; flex: 1; min-width: 0;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}

/* 状态胶囊 */
.rc-status {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 11px; font-weight: 500; padding: 2px 8px;
  border-radius: 20px; white-space: nowrap; flex-shrink: 0;
  .rc-dot { width: 6px; height: 6px; border-radius: 50%; }
  &.dot-free     { background: #f0fdf4; color: #16a34a; .rc-dot { background: #22c55e; } }
  &.dot-occupied { background: #fef2f2; color: #dc2626; .rc-dot { background: #ef4444; } }
  &.dot-disabled { background: #f3f4f6; color: #6b7280; .rc-dot { background: #9ca3af; } }
  &.dot-abnormal { background: #fffbeb; color: #d97706; .rc-dot { background: #f59e0b; } }
}

/* 副标题 */
.rc-sub {
  font-size: 12px; color: #9ca3af; margin-bottom: 10px;
}

.rc-divider {
  height: 1px; background: #f3f4f6; margin-bottom: 10px;
}

/* 容量 + 异常 */
.rc-meta {
  display: flex; align-items: center; gap: 8px; margin-bottom: 8px;
}
.rc-cap {
  font-size: 12px; color: #6b7280;
  display: inline-flex; align-items: center; gap: 4px;
  i { color: #1a56db; font-size: 13px; }
}
.rc-abnormal {
  font-size: 11px; color: #b45309; background: #fef3c7;
  padding: 1px 7px; border-radius: 4px;
  display: inline-flex; align-items: center; gap: 3px;
}

/* 设备标签 */
.rc-equip { display: flex; flex-wrap: wrap; gap: 4px; min-height: 20px; }
.rc-eq-tag {
  font-size: 11px; padding: 1px 7px; border-radius: 10px;
  font-weight: 500; border: 1px solid transparent;
  &.is-damaged { opacity: .5; text-decoration: line-through; }
}
.eq-display { background: #eff6ff; color: #1d4ed8; border-color: #bfdbfe; }
.eq-video   { background: #fdf4ff; color: #7e22ce; border-color: #e9d5ff; }
.eq-audio   { background: #fff7ed; color: #c2410c; border-color: #fed7aa; }
.eq-board   { background: #f0fdf4; color: #15803d; border-color: #a7f3d0; }
.eq-net     { background: #f0f9ff; color: #0369a1; border-color: #bae6fd; }
.eq-default { background: #f9fafb; color: #4b5563; border-color: #e5e7eb; }
.rc-no-eq   { font-size: 12px; color: #d1d5db; }

/* ── 详情弹窗 ── */
.detail-section-title {
  font-size: 13px; font-weight: 600; color: #374151;
  margin-bottom: 10px; border-left: 3px solid #1a56db; padding-left: 8px;
}

/* 会议类型徽章 */
.mms-cat-badge {
  font-size: 11px; padding: 1px 8px; border-radius: 10px; font-weight: 500;
}
.cat-生产 { background:#eaf4ee; color:#2e6347; border:1px solid #5a9472; }
.cat-周边 { background:#e5eef6; color:#2c5578; border:1px solid #4d7fa8; }
.cat-研发 { background:#ebe8f5; color:#443478; border:1px solid #7560a8; }
.cat-业务 { background:#f5ece4; color:#7a3e18; border:1px solid #b87048; }

.room-empty {
  grid-column: 1 / -1;
  text-align: center; color: #9ca3af; padding: 60px 0; font-size: 14px;
}
</style>
