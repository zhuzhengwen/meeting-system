<template>
  <div class="pf-wrap">
    <el-row :gutter="20">

      <!-- 左侧信息卡 -->
      <el-col :span="7" :xs="24">
        <div class="pf-card pf-info-card">

          <!-- 头像 -->
          <div class="pf-avatar-zone">
            <userAvatar :user="user" />
            <div class="pf-name">{{ user.nickName || user.userName }}</div>
            <div class="pf-account">@{{ user.userName }}</div>
          </div>

          <!-- 徽章 -->
          <div class="pf-badges" v-if="user.dept || roleGroup">
            <span class="pf-badge pf-badge-dept" v-if="user.dept">
              <i class="el-icon-office-building"></i>{{ user.dept.deptName }}
            </span>
            <span class="pf-badge pf-badge-role" v-if="roleGroup">
              <i class="el-icon-s-custom"></i>{{ roleGroup }}
            </span>
            <span class="pf-badge pf-badge-ldap" v-if="user.loginType === '1'">
              <i class="el-icon-connection"></i>域账号
            </span>
          </div>

          <!-- 信息行 -->
          <ul class="pf-info-rows">
            <li v-if="user.phonenumber">
              <i class="el-icon-mobile-phone"></i>
              <span>{{ user.phonenumber }}</span>
            </li>
            <li v-if="user.email">
              <i class="el-icon-message"></i>
              <span class="pf-email">{{ user.email }}</span>
            </li>
            <li v-if="postGroup">
              <i class="el-icon-postcard"></i>
              <span>{{ postGroup }}</span>
            </li>
            <li v-if="user.createTime">
              <i class="el-icon-time"></i>
              <span>{{ user.createTime.slice(0, 10) }} 加入</span>
            </li>
          </ul>

        </div>
      </el-col>

      <!-- 右侧编辑卡 -->
      <el-col :span="17" :xs="24">
        <div class="pf-card pf-edit-card">
          <el-tabs v-model="activeTab" class="pf-tabs">
            <el-tab-pane name="userinfo">
              <span slot="label"><i class="el-icon-edit-outline"></i> 基本资料</span>
              <userInfo :user="user" />
            </el-tab-pane>
            <el-tab-pane v-if="user.loginType !== '1'" name="resetPwd">
              <span slot="label"><i class="el-icon-lock"></i> 修改密码</span>
              <resetPwd :user="user" />
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-col>

    </el-row>
  </div>
</template>

<script>
import userAvatar from "./userAvatar";
import userInfo   from "./userInfo";
import resetPwd   from "./resetPwd";
import { getUserProfile } from "@/api/system/user";

export default {
  name: "Profile",
  components: { userAvatar, userInfo, resetPwd },
  data() {
    return { user: {}, roleGroup: "", postGroup: "", activeTab: "userinfo" };
  },
  created() {
    getUserProfile().then(r => {
      this.user      = r.data;
      this.roleGroup = r.roleGroup;
      this.postGroup = r.postGroup;
    });
  }
};
</script>

<style lang="scss" scoped>
.pf-wrap {
  padding: 20px;
}

.pf-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e8eef8;
  box-shadow: 0 2px 12px rgba(13, 31, 78, .06);
}

/* ── 左侧信息卡 ── */
.pf-info-card {
  padding: 28px 20px 20px;
  text-align: center;
}

.pf-avatar-zone {
  margin-bottom: 16px;
}

.pf-name {
  font-size: 17px;
  font-weight: 700;
  color: #111827;
  margin-top: 12px;
  margin-bottom: 3px;
}

.pf-account {
  font-size: 12px;
  color: #9ca3af;
}

/* 徽章 */
.pf-badges {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 6px;
  margin: 14px 0;
  padding: 14px 0;
  border-top: 1px solid #f1f5f9;
  border-bottom: 1px solid #f1f5f9;
}

.pf-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 20px;

  i { font-size: 12px; }

  &.pf-badge-dept { background: #eff6ff; color: #1d4ed8; }
  &.pf-badge-role { background: #f0fdf4; color: #15803d; }
  &.pf-badge-ldap { background: #fdf4ff; color: #7e22ce; }
}

/* 信息行 */
.pf-info-rows {
  list-style: none;
  margin: 0;
  padding: 0;
  text-align: left;

  li {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 9px 4px;
    border-bottom: 1px solid #f9fafb;
    font-size: 13px;
    color: #374151;

    &:last-child { border-bottom: none; }

    i {
      font-size: 15px;
      color: #1a56db;
      flex-shrink: 0;
      width: 18px;
      text-align: center;
    }

    .pf-email {
      font-size: 12px;
      word-break: break-all;
    }
  }
}

/* ── 右侧编辑卡 ── */
.pf-edit-card {
  padding: 0 24px 28px;
  min-height: 320px;
}

.pf-tabs {
  :deep(.el-tabs__header) { margin-bottom: 0; }

  :deep(.el-tabs__item) {
    height: 52px;
    line-height: 52px;
    font-size: 14px;
    color: #6b7280;
    padding: 0 20px;
    i { margin-right: 5px; }
    &.is-active { color: #1a56db; font-weight: 600; }
  }

  :deep(.el-tabs__active-bar) { background: #1a56db; }
  :deep(.el-tabs__nav-wrap::after) { height: 1px; background: #f1f5f9; }
  :deep(.el-tabs__content) { padding-top: 20px; }
}
</style>
