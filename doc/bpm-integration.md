# 会议系统 × BPM 对接文档

**版本：** v1.0  
**日期：** 2026-04-23  
**维护方：** 会议系统开发团队

---

## 目录

1. [概述](#1-概述)
2. [流程说明](#2-流程说明)
3. [会议系统 → BPM（发起审批）](#3-会议系统--bpm发起审批)
4. [BPM → 会议系统（审批结果回调）](#4-bpm--会议系统审批结果回调)
5. [字段枚举说明](#5-字段枚举说明)
6. [安全机制](#6-安全机制)
7. [错误处理约定](#7-错误处理约定)
8. [联调对接清单](#8-联调对接清单)

---

## 1. 概述

会议系统在用户提交预约后，需通过 BPM 完成审批流转。双方通过两个 HTTP 接口交互：

| 方向 | 调用方 | 被调用方 | 时机 |
|------|--------|----------|------|
| 发起审批 | 会议系统 | BPM | 用户提交会议预约后 |
| 审批回调 | BPM | 会议系统 | BPM 审批流程结束后 |

**业务主键：** 使用 `meetingNo`（会议编号）作为两侧关联唯一键，格式为 `MTG-YYYYMMDD-HHmmss-XXXXXX`，例如 `MTG-20260423-143052-A1B2C3`。

---

## 2. 流程说明

```
用户提交预约
     │
     ▼
会议系统保存会议记录
（status = 3，待审批）
     │
     ▼
会议系统 POST /process/start ──► BPM
     │                            │
     │  返回 processInstanceId    │
     │◄───────────────────────────┘
     │
     ▼
会议系统更新 processInstanceId
     │
     │        BPM 内部审批流转
     │
     ▼
BPM POST /mms/bpm/callback ──► 会议系统
     │                              │
     │     返回 {"code":200}        │
     │◄─────────────────────────────┘
     │
     ▼
会议系统更新会议状态
  审批通过 → status = 0（已确认）
  审批拒绝 → status = 4（审批拒绝）
```

**会议状态枚举：**

| 值 | 含义 |
|----|------|
| `0` | 已确认（审批通过 或 未启用审批时直接生效） |
| `1` | 已取消 |
| `2` | 已完成 |
| `3` | 待审批 |
| `4` | 审批拒绝 |

---

## 3. 会议系统 → BPM（发起审批）

### 接口信息

| 项目 | 内容 |
|------|------|
| 方法 | `POST` |
| 路径 | `{bpm_base_url}/process/start` |
| Content-Type | `application/json` |
| 调用时机 | 用户提交会议预约、会议记录落库后 |

> `bpm_base_url` 由 BPM 方提供，配置在会议系统 `application.yml` 的 `bpm.url`。

---

### 请求体

```json
{
  "processDefKey": "meeting_approval",
  "bizKey":        "MTG-20260423-143052-A1B2C3",
  "title":         "会议预约审批 — 季度复盘会议",
  "initiator":     "zhangsan",
  "initiatorName": "张三",
  "formData": {
    "meetingId":   123,
    "meetingNo":   "MTG-20260423-143052-A1B2C3",
    "title":       "季度复盘会议",
    "campus":      "1园",
    "roomNumber":  "A101",
    "startTime":   "2026-04-25 09:00:00",
    "endTime":     "2026-04-25 11:00:00",
    "hostName":    "张三",
    "leadDept":    "研发部",
    "meetingType": "0",
    "description": "Q1复盘，需提前准备数据报告"
  },
  "callbackUrl":   "http://meeting.example.com/mms/bpm/callback",
  "callbackToken": "your-shared-secret-token"
}
```

#### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `processDefKey` | String | 是 | 流程定义 Key，固定值 `meeting_approval`，双方提前约定 |
| `bizKey` | String | 是 | 业务主键，即 `meetingNo`，BPM 侧需原样保存，回调时返回 |
| `title` | String | 是 | 审批任务标题，在 BPM 待办列表中展示 |
| `initiator` | String | 是 | 发起人账号（系统登录名） |
| `initiatorName` | String | 是 | 发起人姓名 |
| `formData` | Object | 是 | 审批表单数据，BPM 在审批页面展示，详见下表 |
| `callbackUrl` | String | 是 | 审批完成后 BPM 回调的完整地址 |
| `callbackToken` | String | 是 | 鉴权 Token，BPM 回调时需原样放入请求体的 `token` 字段 |

#### formData 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `meetingId` | Long | 会议数据库主键 |
| `meetingNo` | String | 会议编号 |
| `title` | String | 会议标题 |
| `campus` | String | 园区（如 `1园`、`2园`） |
| `roomNumber` | String | 会议室编号（如 `A101`） |
| `startTime` | String | 开始时间，格式 `yyyy-MM-dd HH:mm:ss` |
| `endTime` | String | 结束时间，格式 `yyyy-MM-dd HH:mm:ss` |
| `hostName` | String | 主持人姓名 |
| `leadDept` | String | 主导部门 |
| `meetingType` | String | 会议形式：`0`=线下，`1`=线上 |
| `description` | String | 会议说明，可为空 |

---

### 响应体

**成功（HTTP 200）：**

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "processInstanceId": "meeting_approval:1:20260423001"
  }
}
```

**失败（HTTP 200，code 非 0）：**

```json
{
  "code": 500,
  "msg": "流程定义不存在"
}
```

#### 响应字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | Integer | `0` 表示成功，其他值表示失败 |
| `msg` | String | 描述信息 |
| `data.processInstanceId` | String | BPM 流程实例 ID，会议系统用于溯源，**必须返回** |

> **注意：** 若 BPM 调用失败或超时，会议系统不会回滚，会议记录保持 `status=3`（待审批）。可由管理员人工补发或 BPM 侧重试。

---

## 4. BPM → 会议系统（审批结果回调）

### 接口信息

| 项目 | 内容 |
|------|------|
| 方法 | `POST` |
| 完整路径 | `http://meeting.example.com/mms/bpm/callback` |
| Content-Type | `application/json` |
| 鉴权方式 | 请求体携带 `token` 字段（共享密钥），**不需要** Authorization Header |
| 调用时机 | BPM 审批流程结束时（通过 或 拒绝） |

---

### 请求体

```json
{
  "bizKey":            "MTG-20260423-143052-A1B2C3",
  "processInstanceId": "meeting_approval:1:20260423001",
  "result":            "approved",
  "comment":           "同意，请提前准备会议材料",
  "approver":          "李四",
  "approveTime":       "2026-04-24 10:30:00",
  "token":             "your-shared-secret-token"
}
```

#### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `bizKey` | String | 是 | 发起时传入的 `bizKey`（即 `meetingNo`），原样返回 |
| `processInstanceId` | String | 是 | BPM 流程实例 ID |
| `result` | String | 是 | 审批结果：`approved`（通过）或 `rejected`（拒绝） |
| `comment` | String | 否 | 审批意见 |
| `approver` | String | 否 | 最终审批人姓名 |
| `approveTime` | String | 否 | 审批完成时间，格式 `yyyy-MM-dd HH:mm:ss` |
| `token` | String | 是 | 鉴权 Token，必须与发起时的 `callbackToken` 一致 |

---

### 响应体

**成功（HTTP 200）：**

```json
{
  "code": 200,
  "msg": "操作成功"
}
```

**失败（HTTP 200）：**

```json
{
  "code": 500,
  "msg": "回调 Token 不合法"
}
```

#### 常见错误响应

| msg | 原因 | 处理建议 |
|-----|------|----------|
| `回调 Token 不合法` | token 字段与约定不一致 | 检查双方 Token 配置是否一致 |
| `会议不存在: MTG-xxx` | bizKey 对应的会议未找到 | 确认 bizKey 是否原样返回 |
| `处理回调失败，请联系系统管理员` | 系统内部异常 | 联系会议系统管理员排查日志 |

> **重要：** 建议 BPM 侧在收到非 200 HTTP 状态码、或 `code != 200` 时，按指数退避策略重试，最多重试 3 次。

---

## 5. 字段枚举说明

### meetingType（会议形式）

| 值 | 含义 |
|----|------|
| `0` | 线下会议（需预约会议室） |
| `1` | 线上会议（腾讯会议等） |

### result（审批结果，BPM 回调）

| 值 | 含义 | 会议系统状态变更 |
|----|------|-----------------|
| `approved` | 审批通过 | `status: 3 → 0` |
| `rejected` | 审批拒绝 | `status: 3 → 4` |

---

## 6. 安全机制

### 共享 Token 鉴权

- 会议系统与 BPM 在部署时通过运维渠道约定一个强随机字符串作为 `callbackToken`
- 会议系统发起审批时将 Token 传给 BPM（字段：`callbackToken`）
- BPM 回调时将 Token 放入请求体（字段：`token`）
- 会议系统收到回调后做字符串全等校验，不一致则拒绝处理并返回 500

**Token 生成建议：**
```bash
# 生成 32 位随机 Token
openssl rand -hex 32
# 示例输出：a3f8c2d1e4b7...
```

### IP 白名单（可选加固）

建议在网络层限制 `/mms/bpm/callback` 只允许 BPM 服务器 IP 访问，作为 Token 鉴权之外的第二道防线。

### HTTPS

生产环境双向接口均应走 HTTPS，防止 Token 在传输中泄露。

---

## 7. 错误处理约定

| 场景 | 会议系统行为 | BPM 侧建议 |
|------|-------------|------------|
| 发起审批超时（>10s） | 记录错误日志，会议保持 `status=3` | — |
| 发起审批 HTTP 非 2xx | 同上 | — |
| 回调 Token 校验失败 | 返回 500，不更新会议状态 | 检查 Token 配置后重试 |
| 回调 bizKey 不存在 | 返回 500 | 确认 bizKey 是否正确 |
| 回调时会议已非待审批状态 | 忽略本次回调，返回 200 | 无需重试 |

---

## 8. 联调对接清单

在正式联调前，请双方确认以下事项：

### BPM 侧需提供

- [ ] BPM 服务地址（`bpm_base_url`），如 `http://bpm.example.com`
- [ ] 发起审批接口路径，确认是否为 `/process/start`
- [ ] 响应体结构，确认 `processInstanceId` 的路径（如 `data.processInstanceId`）
- [ ] 流程定义 Key，确认是否使用 `meeting_approval`
- [ ] 约定 `callbackToken` 值

### 会议系统侧需提供

- [ ] 回调地址（`callbackUrl`），如 `http://meeting.example.com/mms/bpm/callback`
- [ ] 约定 `callbackToken` 值（与 BPM 侧一致）
- [ ] 审批表单字段清单（见第 3 节 formData）
- [ ] 测试用 `meetingNo` 示例

### 联调验证步骤

1. **发起审批验证**：提交一条测试会议预约，检查 BPM 侧是否收到请求，`bizKey` 是否正确
2. **回调验证**：在 BPM 侧手动触发审批通过回调，检查会议状态是否变为 `0`（已确认）
3. **拒绝验证**：触发审批拒绝回调，检查会议状态是否变为 `4`（审批拒绝）
4. **Token 校验验证**：使用错误 Token 发起回调，确认系统返回 500

---

*如有接口变更需求，请双方提前沟通并同步更新本文档。*
