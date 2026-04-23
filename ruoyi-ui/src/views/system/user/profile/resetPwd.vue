<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="90px" class="rp-form">

    <div class="rp-section-title">身份验证</div>

    <el-form-item label="当前密码" prop="oldPassword">
      <el-input v-model="user.oldPassword" type="password" placeholder="请输入当前密码" show-password style="max-width:320px" />
    </el-form-item>

    <div class="rp-section-title" style="margin-top:20px">设置新密码</div>

    <el-form-item label="新密码" prop="newPassword">
      <el-input v-model="user.newPassword" type="password" placeholder="6–20 位字符" show-password style="max-width:320px" />
      <div class="rp-strength" v-if="user.newPassword">
        <div class="rp-bars">
          <span v-for="n in 4" :key="n" class="rp-bar" :class="{ on: strengthLevel >= n, [`lv${strengthLevel}`]: strengthLevel >= n }"></span>
        </div>
        <span class="rp-strength-text" :class="`lv${strengthLevel}`">{{ strengthText }}</span>
      </div>
      <div class="rp-pwd-tips">建议包含大小写字母、数字及特殊字符</div>
    </el-form-item>

    <el-form-item label="确认密码" prop="confirmPassword">
      <el-input v-model="user.confirmPassword" type="password" placeholder="再次输入新密码" show-password style="max-width:320px" />
    </el-form-item>

    <div class="rp-actions">
      <el-button type="primary" icon="el-icon-check" @click="submit">确认修改</el-button>
      <el-button icon="el-icon-close" @click="close">取消</el-button>
    </div>

  </el-form>
</template>

<script>
import { updateUserPwd } from "@/api/system/user";

export default {
  data() {
    const confirmValidator = (rule, value, callback) => {
      if (this.user.newPassword !== value) callback(new Error("两次密码不一致"));
      else callback();
    };
    return {
      user: { oldPassword: undefined, newPassword: undefined, confirmPassword: undefined },
      rules: {
        oldPassword:     [{ required: true, message: "请输入当前密码", trigger: "blur" }],
        newPassword:     [{ required: true, message: "请输入新密码", trigger: "blur" }, { min: 6, max: 20, message: "长度 6–20 位", trigger: "blur" }],
        confirmPassword: [{ required: true, message: "请确认密码", trigger: "blur" }, { validator: confirmValidator, trigger: "blur" }]
      }
    };
  },
  computed: {
    strengthLevel() {
      const p = this.user.newPassword || "";
      if (!p) return 0;
      let s = 0;
      if (p.length >= 8)           s++;
      if (/[A-Z]/.test(p))         s++;
      if (/[0-9]/.test(p))         s++;
      if (/[^A-Za-z0-9]/.test(p))  s++;
      return Math.max(1, s);
    },
    strengthText() {
      return ["", "弱", "一般", "较强", "强"][this.strengthLevel];
    }
  },
  methods: {
    submit() {
      this.$refs.form.validate(valid => {
        if (valid) updateUserPwd(this.user.oldPassword, this.user.newPassword).then(() => this.msgSuccess("密码修改成功"));
      });
    },
    close() {
      this.$store.dispatch("tagsView/delView", this.$route);
      this.$router.push({ path: "/index" });
    }
  }
};
</script>

<style lang="scss" scoped>
.rp-form {
  max-width: 560px;

  .rp-section-title {
    font-size: 13px;
    font-weight: 600;
    color: #9ca3af;
    text-transform: uppercase;
    letter-spacing: .5px;
    margin-bottom: 14px;
    padding-left: 10px;
    border-left: 3px solid #1a56db;
  }

  .rp-strength {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 8px;
    max-width: 320px;

    .rp-bars {
      display: flex;
      gap: 4px;
      flex: 1;

      .rp-bar {
        flex: 1;
        height: 4px;
        border-radius: 2px;
        background: #e5e7eb;
        transition: background .2s;

        &.on {
          &.lv1 { background: #ef4444; }
          &.lv2 { background: #f59e0b; }
          &.lv3 { background: #10b981; }
          &.lv4 { background: #1a56db; }
        }
      }
    }

    .rp-strength-text {
      font-size: 11px;
      font-weight: 600;
      width: 28px;
      text-align: right;
      flex-shrink: 0;
      &.lv1 { color: #ef4444; }
      &.lv2 { color: #f59e0b; }
      &.lv3 { color: #10b981; }
      &.lv4 { color: #1a56db; }
    }
  }

  .rp-pwd-tips {
    font-size: 11px;
    color: #9ca3af;
    margin-top: 5px;
  }

  .rp-actions {
    margin-top: 24px;
    padding-top: 18px;
    border-top: 1px solid #f1f5f9;
    display: flex;
    gap: 10px;
  }
}
</style>
