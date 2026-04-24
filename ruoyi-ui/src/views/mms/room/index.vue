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

        <!-- 状态指示条 -->
        <div :class="['rc-bar', statusDotClass(room)]"></div>

        <div class="rc-body">
          <!-- 名称 + 容量 -->
          <div class="rc-header">
            <span class="rc-name">{{ room.roomName || room.roomNumber }}</span>
            <span class="rc-cap"><i class="el-icon-user"></i>{{ room.capacity }}</span>
          </div>

          <!-- 编号 -->
          <div class="rc-no">{{ room.campus }} {{ room.roomNumber }}</div>

          <!-- 设备 -->
          <div class="rc-equip">
            <span v-for="eq in splitEq(room.equipment)" :key="eq"
              :class="['rc-eq-tag', eqClass(eq), isDamaged(room, eq) ? 'is-damaged' : '']">{{ eq }}</span>
            <span v-if="!room.equipment" class="rc-no-eq">暂无设备</span>
          </div>

          <!-- 底部状态 -->
          <div class="rc-footer">
            <span :class="['rc-state', statusDotClass(room)]">
              <i class="rc-dot"></i>{{ statusText(room) }}
            </span>
            <span v-if="room.isAbnormal === '1'" class="rc-warn">
              <i class="el-icon-warning-outline"></i>设备异常
            </span>
          </div>
        </div>
      </div>

      <div v-if="!loading && roomList.length === 0" class="room-empty">
        暂无会议室数据
      </div>
    </div>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- ── 会议室详情弹窗 ── -->
    <el-dialog :visible.sync="detailVisible" width="680px" append-to-body custom-class="room-detail-dialog" :show-title="false">
      <div slot="title" style="display:none"></div>

      <!-- 弹窗头部：色条 + 名称信息 -->
      <div :class="['rd-head', 'rc-' + statusDotClass(detail)]">
        <div class="rd-head-bar"></div>
        <div class="rd-head-body">
          <div class="rd-head-left">
            <div class="rd-title">{{ detail.roomName || detail.roomNumber }}</div>
            <div class="rd-sub">{{ detail.campus }} · {{ detail.roomNumber }}</div>
          </div>
          <div class="rd-head-right">
            <span :class="['rd-status', statusDotClass(detail)]">
              <i class="rd-dot"></i>{{ statusText(detail) }}
            </span>
          </div>
        </div>
      </div>

      <!-- 基本信息卡片 -->
      <div class="rd-info-row">
        <div class="rd-info-item">
          <i class="el-icon-user rd-icon"></i>
          <span class="rd-info-label">容量</span>
          <span class="rd-info-val">{{ detail.capacity }} 人</span>
        </div>
        <div class="rd-info-item">
          <i class="el-icon-office-building rd-icon"></i>
          <span class="rd-info-label">园区</span>
          <span class="rd-info-val">{{ detail.campus }}</span>
        </div>
        <div class="rd-info-item">
          <i class="el-icon-document rd-icon"></i>
          <span class="rd-info-label">备注</span>
          <span class="rd-info-val">{{ detail.remark || '—' }}</span>
        </div>
      </div>

      <!-- 设备清单 -->
      <div class="rd-section">
        <div class="rd-section-title"><i class="el-icon-cpu"></i> 设备清单</div>
        <div class="rd-equip">
          <span v-for="eq in splitEq(detail.equipment)" :key="eq"
            :class="['rc-eq-tag', eqClass(eq), isDamaged(detail, eq) ? 'is-damaged' : '']">
            {{ eq }}
          </span>
          <span v-if="!detail.equipment" class="rc-no-eq">暂无设备信息</span>
        </div>
        <div v-if="detail.isAbnormal === '1'" class="rd-abnormal-tip">
          <i class="el-icon-warning"></i>
          存在异常设备，当前不可预约
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="rd-actions">
        <div class="rd-actions-left">
          <el-button size="small" icon="el-icon-edit" @click="handleUpdate(detail)" v-hasPermi="['mms:room:edit']">修改信息</el-button>
          <el-button size="small" icon="el-icon-s-check" @click="openInspect(null)">日常点检</el-button>
          <el-button v-if="detail.isAbnormal === '1'" size="small" icon="el-icon-circle-check" type="success" plain @click="clearAbnormal">解除异常</el-button>
        </div>
        <el-button size="small" icon="el-icon-delete" type="danger" plain @click="handleDelete(detail)" v-hasPermi="['mms:room:remove']">删除</el-button>
      </div>

      <!-- 使用记录 -->
      <div class="rd-section">
        <div class="rd-section-title"><i class="el-icon-time"></i> 使用记录（近20条）</div>
        <el-table :data="roomMeetings" size="small" v-loading="meetingLoading"
          style="width:100%" :header-cell-style="{background:'#f8faff',color:'#374151',fontWeight:600}">
          <el-table-column label="日期时间" width="165">
            <template slot-scope="s">{{ fmtRange(s.row.startTime, s.row.endTime) }}</template>
          </el-table-column>
          <el-table-column label="会议主题" prop="title" show-overflow-tooltip />
          <el-table-column label="类型" width="68">
            <template slot-scope="s">
              <span :class="['mms-cat-badge', 'cat-' + s.row.category]">{{ s.row.category }}</span>
            </template>
          </el-table-column>
          <el-table-column label="主持人" prop="hostName" width="90" />
          <el-table-column label="状态" width="76">
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
      </div>
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
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 20px;
}

/* 卡片本体 */
.room-card {
  background: #fff;
  border: 1px solid #e4eaf5;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 168px;
  transition: box-shadow .2s, transform .15s;
  &:hover {
    box-shadow: 0 4px 18px rgba(26,86,219,.12);
    transform: translateY(-2px);
  }
}

/* 顶部状态色条 */
.rc-bar {
  height: 4px; flex-shrink: 0;
  &.dot-free     { background: linear-gradient(90deg, #1a56db, #60a5fa); }
  &.dot-occupied { background: linear-gradient(90deg, #ef4444, #f87171); }
  &.dot-disabled { background: #d1d5db; }
  &.dot-abnormal { background: linear-gradient(90deg, #f59e0b, #fcd34d); }
}

.rc-body {
  padding: 12px 14px 0;
  display: flex; flex-direction: column; flex: 1; min-height: 0;
}

/* 名称 + 容量 */
.rc-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 2px; flex-shrink: 0;
}
.rc-name {
  font-size: 14px; font-weight: 700; color: #111827;
  flex: 1; min-width: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.rc-cap {
  font-size: 11px; color: #6b7280; flex-shrink: 0; margin-left: 6px;
  display: flex; align-items: center; gap: 2px;
  i { font-size: 11px; }
}

/* 编号 */
.rc-no {
  font-size: 11px; color: #9ca3af; margin-bottom: 8px; flex-shrink: 0;
}

/* 设备标签区域 —— flex:1 撑开，让底部 footer 始终贴底 */
.rc-equip {
  flex: 1; display: flex; flex-wrap: wrap; align-content: flex-start;
  gap: 3px; overflow: hidden;
}
.rc-eq-tag {
  font-size: 11px; padding: 1px 7px; border-radius: 20px;
  font-weight: 500; line-height: 1.6; white-space: nowrap;
  border: 1px solid transparent;
  &.eq-display { background: #eff6ff; color: #1d4ed8; border-color: #bfdbfe; }
  &.eq-video   { background: #fdf4ff; color: #7e22ce; border-color: #e9d5ff; }
  &.eq-audio   { background: #fff7ed; color: #c2410c; border-color: #fed7aa; }
  &.eq-board   { background: #f0fdf4; color: #15803d; border-color: #bbf7d0; }
  &.eq-net     { background: #f0f9ff; color: #0369a1; border-color: #bae6fd; }
  &.eq-default { background: #f5f3ff; color: #5b21b6; border-color: #ddd6fe; }
  &.is-damaged { opacity: .4; text-decoration: line-through; }
}
.rc-no-eq { font-size: 11px; color: #d1d5db; align-self: flex-start; }

/* 底部状态行 —— 固定贴底 */
.rc-footer {
  display: flex; align-items: center; justify-content: space-between;
  padding: 7px 0 10px; flex-shrink: 0;
  border-top: 1px solid #f3f4f6; margin-top: 8px;
}
.rc-state {
  display: inline-flex; align-items: center; gap: 5px;
  font-size: 11px; font-weight: 500;
  .rc-dot { width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }
  &.dot-free     { color: #1a56db; .rc-dot { background: #1a56db; } }
  &.dot-occupied { color: #dc2626; .rc-dot { background: #ef4444; } }
  &.dot-disabled { color: #9ca3af; .rc-dot { background: #d1d5db; } }
  &.dot-abnormal { color: #d97706; .rc-dot { background: #f59e0b; } }
}
.rc-warn {
  font-size: 11px; color: #d97706;
  display: flex; align-items: center; gap: 3px;
}

/* ── 详情弹窗 ── */
.rd-head {
  margin: -20px -20px 0;
  overflow: hidden;
  border-radius: 0;
}
.rd-head-bar {
  height: 4px;
  .rc-dot-free     & { background: linear-gradient(90deg, #1a56db, #60a5fa); }
  .rc-dot-occupied & { background: linear-gradient(90deg, #ef4444, #f87171); }
  .rc-dot-disabled & { background: #d1d5db; }
  .rc-dot-abnormal & { background: linear-gradient(90deg, #f59e0b, #fcd34d); }
}
.rd-head-body {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 20px 14px; background: #f8faff;
  border-bottom: 1px solid #e8eef8;
}
.rd-title { font-size: 17px; font-weight: 700; color: #111827; margin-bottom: 3px; }
.rd-sub   { font-size: 12px; color: #9ca3af; }
.rd-status {
  display: inline-flex; align-items: center; gap: 5px;
  font-size: 12px; font-weight: 600; padding: 4px 12px;
  border-radius: 20px;
  .rd-dot { width: 7px; height: 7px; border-radius: 50%; }
  &.dot-free     { background: #eff6ff; color: #1a56db; .rd-dot { background: #1a56db; } }
  &.dot-occupied { background: #fef2f2; color: #dc2626; .rd-dot { background: #ef4444; } }
  &.dot-disabled { background: #f3f4f6; color: #6b7280; .rd-dot { background: #d1d5db; } }
  &.dot-abnormal { background: #fffbeb; color: #d97706; .rd-dot { background: #f59e0b; } }
}

/* 基本信息横排 */
.rd-info-row {
  display: flex; gap: 0; margin: 16px 0 0;
  border: 1px solid #e8eef8; border-radius: 8px; overflow: hidden;
}
.rd-info-item {
  flex: 1; display: flex; flex-direction: column; align-items: center;
  padding: 12px 8px; gap: 4px;
  border-right: 1px solid #e8eef8;
  &:last-child { border-right: none; }
}
.rd-icon { font-size: 16px; color: #1a56db; }
.rd-info-label { font-size: 11px; color: #9ca3af; }
.rd-info-val   { font-size: 13px; font-weight: 600; color: #1f2937; }

/* 分区 */
.rd-section { margin-top: 18px; }
.rd-section-title {
  font-size: 13px; font-weight: 600; color: #374151;
  margin-bottom: 10px; display: flex; align-items: center; gap: 6px;
  i { color: #1a56db; }
}
.rd-equip { display: flex; flex-wrap: wrap; gap: 5px; }
.rd-abnormal-tip {
  margin-top: 8px; font-size: 12px; color: #d97706;
  background: #fffbeb; border: 1px solid #fde68a;
  border-radius: 6px; padding: 6px 10px;
  display: flex; align-items: center; gap: 5px;
}

/* 操作栏 */
.rd-actions {
  display: flex; justify-content: space-between; align-items: center;
  margin-top: 16px; padding: 12px 0;
  border-top: 1px solid #f3f4f6; border-bottom: 1px solid #f3f4f6;
}
.rd-actions-left { display: flex; gap: 8px; }

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

<style>
/* 详情弹窗：去掉默认 header，body 顶部无内边距 */
.room-detail-dialog .el-dialog__header { display: none; }
.room-detail-dialog .el-dialog__body  { padding: 20px; }
</style>
