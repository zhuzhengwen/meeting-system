<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="90px" class="ui-form">

    <div class="ui-section-title">基础信息</div>

    <div class="ui-grid">
      <el-form-item label="用户昵称" prop="nickName">
        <el-input v-model="user.nickName" placeholder="请输入昵称" />
      </el-form-item>

      <el-form-item label="手机号码" prop="phonenumber">
        <el-input v-model="user.phonenumber" maxlength="11" placeholder="请输入手机号码" />
      </el-form-item>

      <el-form-item label="邮箱地址" prop="email">
        <el-input v-model="user.email" maxlength="50" placeholder="请输入邮箱" />
      </el-form-item>

      <el-form-item label="性别">
        <el-radio-group v-model="user.sex">
          <el-radio label="0">男</el-radio>
          <el-radio label="1">女</el-radio>
        </el-radio-group>
      </el-form-item>
    </div>

    <div class="ui-actions">
      <el-button type="primary" icon="el-icon-check" @click="submit">保存修改</el-button>
      <el-button icon="el-icon-close" @click="close">取消</el-button>
    </div>

  </el-form>
</template>

<script>
import { updateUserProfile } from "@/api/system/user";

export default {
  props: { user: { type: Object } },
  data() {
    return {
      rules: {
        nickName: [{ required: true, message: "昵称不能为空", trigger: "blur" }],
        email: [
          { required: true, message: "邮箱不能为空", trigger: "blur" },
          { type: "email", message: "请输入正确的邮箱", trigger: ["blur", "change"] }
        ],
        phonenumber: [
          { required: true, message: "手机号不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    submit() {
      this.$refs.form.validate(valid => {
        if (valid) updateUserProfile(this.user).then(() => this.msgSuccess("修改成功"));
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
.ui-form {
  max-width: 680px;

  .ui-section-title {
    font-size: 13px;
    font-weight: 600;
    color: #9ca3af;
    text-transform: uppercase;
    letter-spacing: .5px;
    margin-bottom: 16px;
    padding-left: 10px;
    border-left: 3px solid #1a56db;
  }

  .ui-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 4px 32px;

    @media (max-width: 600px) { grid-template-columns: 1fr; }
  }

  .ui-actions {
    margin-top: 20px;
    padding-top: 18px;
    border-top: 1px solid #f1f5f9;
    display: flex;
    gap: 10px;
  }
}
</style>
