<template>
  <div class="mms-login">

    <!-- 左侧品牌面板 -->
    <div class="mms-brand">
      <div class="mms-brand-inner">
        <div class="mms-hero-icon">
          <svg viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
            <ellipse cx="60" cy="68" rx="36" ry="20" fill="rgba(255,255,255,0.15)" stroke="rgba(255,255,255,0.6)" stroke-width="2"/>
            <ellipse cx="60" cy="66" rx="22" ry="11" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.25)" stroke-width="1"/>
            <circle cx="36" cy="40" r="8" fill="rgba(255,255,255,0.9)"/>
            <circle cx="60" cy="34" r="8" fill="rgba(255,255,255,0.9)"/>
            <circle cx="84" cy="40" r="8" fill="rgba(255,255,255,0.9)"/>
            <circle cx="44" cy="96" r="8" fill="rgba(255,255,255,0.7)"/>
            <circle cx="76" cy="96" r="8" fill="rgba(255,255,255,0.7)"/>
            <circle cx="36" cy="38" r="4" fill="#1a56db"/>
            <circle cx="60" cy="32" r="4" fill="#1a56db"/>
            <circle cx="84" cy="38" r="4" fill="#1a56db"/>
            <circle cx="44" cy="94" r="4" fill="#2563eb"/>
            <circle cx="76" cy="94" r="4" fill="#2563eb"/>
          </svg>
        </div>
        <h1 class="mms-brand-title">会议管理系统</h1>
        <p class="mms-brand-sub">Meeting Management System</p>
        <div class="mms-features">
          <div class="mms-feature-item"><span class="mms-feature-dot"></span>统一会议预约与排程</div>
          <div class="mms-feature-item"><span class="mms-feature-dot"></span>智能会议室资源管理</div>
          <div class="mms-feature-item"><span class="mms-feature-dot"></span>精细化部门数据权限</div>
        </div>
      </div>
      <div class="mms-deco mms-deco-1"></div>
      <div class="mms-deco mms-deco-2"></div>
      <div class="mms-deco mms-deco-3"></div>
    </div>

    <!-- 右侧登录面板 -->
    <div class="mms-form-panel">
      <div class="mms-form-wrap">
        <div class="mms-form-header">
          <div class="mms-form-logo">
            <svg viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
              <rect width="40" height="40" rx="8" fill="#1a56db"/>
              <ellipse cx="20" cy="22" rx="10" ry="6" fill="#2563eb"/>
              <ellipse cx="20" cy="22" rx="10" ry="6" fill="none" stroke="white" stroke-width="1.5"/>
              <circle cx="13" cy="14" r="2.2" fill="white"/>
              <circle cx="20" cy="12" r="2.2" fill="white"/>
              <circle cx="27" cy="14" r="2.2" fill="white"/>
              <circle cx="15" cy="31" r="2.2" fill="white" opacity="0.8"/>
              <circle cx="25" cy="31" r="2.2" fill="white" opacity="0.8"/>
            </svg>
          </div>
          <h2 class="mms-form-title">欢迎登录</h2>
          <p class="mms-form-desc">请选择登录方式并填写账号信息</p>
        </div>

        <el-tabs v-model="loginForm.loginType" class="mms-tabs" @tab-click="onTabChange">
          <el-tab-pane label="域账号" name="1" />
          <el-tab-pane label="系统账号" name="0" />
        </el-tabs>

        <el-form ref="loginForm" :model="loginForm" :rules="currentRules" class="mms-el-form">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" type="text" auto-complete="off"
              placeholder="请输入账号" prefix-icon="el-icon-user" size="medium" />
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" auto-complete="off"
              placeholder="请输入密码" prefix-icon="el-icon-lock" size="medium" show-password
              @keyup.enter.native="handleLogin" />
          </el-form-item>

          <el-form-item v-if="loginForm.loginType === '0'" prop="code" class="mms-code-item">
            <el-input v-model="loginForm.code" auto-complete="off" placeholder="请输入验证码"
              prefix-icon="el-icon-key" size="medium" class="mms-code-input"
              @keyup.enter.native="handleLogin" />
            <div class="mms-code-img" @click="getCode">
              <img :src="codeUrl" alt="验证码" />
            </div>
          </el-form-item>

          <div v-if="loginForm.loginType === '0'" class="mms-remember">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
          </div>

          <el-button :loading="loading" type="primary" class="mms-login-btn"
            @click.native.prevent="handleLogin">
            <span v-if="!loading">登&nbsp;&nbsp;录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form>
      </div>

      <div class="mms-form-footer">
        Copyright © 2024 会议管理系统 &nbsp;|&nbsp; All Rights Reserved
      </div>
    </div>

  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: "",
        loginType: "1"
      },
      sysRules: {
        username: [{ required: true, trigger: "blur", message: "请输入账号" }],
        password: [{ required: true, trigger: "blur", message: "请输入密码" }],
        code:     [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      ldapRules: {
        username: [{ required: true, trigger: "blur", message: "请输入LDAP账号" }],
        password: [{ required: true, trigger: "blur", message: "请输入密码" }]
      },
      loading: false,
      redirect: undefined
    };
  },
  computed: {
    currentRules() {
      return this.loginForm.loginType === "1" ? this.ldapRules : this.sysRules;
    }
  },
  watch: {
    $route: {
      handler(route) { this.redirect = route.query && route.query.redirect; },
      immediate: true
    }
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.codeUrl = "data:image/gif;base64," + res.img;
        this.loginForm.uuid = res.uuid;
      });
    },
    getCookie() {
      const username  = Cookies.get("username");
      const password  = Cookies.get("password");
      const rememberMe = Cookies.get("rememberMe");
      if (username) {
        this.loginForm.username   = username;
        this.loginForm.password   = password ? decrypt(password) : "";
        this.loginForm.rememberMe = rememberMe === "true";
      }
    },
    onTabChange() {
      this.$nextTick(() => this.$refs.loginForm.clearValidate());
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return;
        this.loading = true;
        if (this.loginForm.loginType === "0") {
          if (this.loginForm.rememberMe) {
            Cookies.set("username",   this.loginForm.username,                  { expires: 30 });
            Cookies.set("password",   encrypt(this.loginForm.password),         { expires: 30 });
            Cookies.set("rememberMe", true,                                      { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove("rememberMe");
          }
        }
        this.$store.dispatch("Login", this.loginForm).then(() => {
          this.$router.push({ path: this.redirect || "/" }).catch(() => {});
        }).catch(() => {
          this.loading = false;
          if (this.loginForm.loginType === "0") this.getCode();
        });
      });
    }
  }
};
</script>

<style lang="scss">
.mms-login {
  display: flex;
  height: 100vh;
  min-height: 560px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  /* ── 左侧品牌区 ── */
  .mms-brand {
    position: relative;
    width: 45%;
    background: linear-gradient(145deg, #0d1f4e 0%, #1a3a8f 50%, #1d4ed8 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    flex-shrink: 0;
    @media (max-width: 768px) { display: none; }
  }

  .mms-brand-inner {
    position: relative;
    z-index: 2;
    text-align: center;
    padding: 40px;
  }

  .mms-hero-icon {
    width: 150px;
    height: 150px;
    margin: 0 auto 28px;
    background: rgba(255,255,255,0.08);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid rgba(255,255,255,0.15);
    box-shadow: 0 0 60px rgba(29,78,216,0.5), inset 0 0 30px rgba(255,255,255,0.03);
    svg { width: 100px; height: 100px; }
  }

  .mms-brand-title {
    color: #fff;
    font-size: 28px;
    font-weight: 700;
    margin: 0 0 8px;
    letter-spacing: 3px;
  }

  .mms-brand-sub {
    color: rgba(255,255,255,0.45);
    font-size: 12px;
    letter-spacing: 4px;
    margin: 0 0 40px;
    text-transform: uppercase;
  }

  .mms-features { text-align: left; display: inline-block; }

  .mms-feature-item {
    color: rgba(255,255,255,0.78);
    font-size: 14px;
    margin-bottom: 14px;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .mms-feature-dot {
    width: 6px; height: 6px;
    border-radius: 50%;
    background: #60a5fa;
    flex-shrink: 0;
    box-shadow: 0 0 6px #60a5fa;
  }

  .mms-deco {
    position: absolute;
    border-radius: 50%;
    border: 1px solid rgba(255,255,255,0.07);
    pointer-events: none;
  }
  .mms-deco-1 { width: 380px; height: 380px; top: -100px; right: -100px; background: rgba(255,255,255,0.02); }
  .mms-deco-2 { width: 220px; height: 220px; bottom: -50px; left: -50px; background: rgba(255,255,255,0.03); }
  .mms-deco-3 { width: 130px; height: 130px; bottom: 100px; right: 40px; background: rgba(96,165,250,0.05); }

  /* ── 右侧表单区 ── */
  .mms-form-panel {
    flex: 1;
    background: #f1f5f9;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 24px;
  }

  .mms-form-wrap {
    width: 100%;
    max-width: 400px;
    background: #fff;
    border-radius: 16px;
    padding: 40px 40px 32px;
    box-shadow: 0 4px 32px rgba(0,0,0,0.07), 0 1px 4px rgba(0,0,0,0.04);
  }

  .mms-form-header { text-align: center; margin-bottom: 24px; }

  .mms-form-logo {
    width: 54px; height: 54px;
    margin: 0 auto 14px;
    svg { width: 100%; height: 100%; }
  }

  .mms-form-title {
    font-size: 22px;
    font-weight: 700;
    color: #111827;
    margin: 0 0 6px;
  }

  .mms-form-desc {
    font-size: 13px;
    color: #9ca3af;
    margin: 0;
  }

  .mms-tabs {
    margin-bottom: 20px;
    .el-tabs__header { margin-bottom: 0; }
    .el-tabs__nav-wrap::after { height: 1px; background: #f3f4f6; }
    .el-tabs__item {
      font-size: 14px;
      color: #6b7280;
      padding: 0 20px;
      &.is-active { color: #1a56db; font-weight: 600; }
    }
    .el-tabs__active-bar { background: #1a56db; }
  }

  .mms-el-form {
    .el-input__inner {
      height: 44px;
      border-radius: 8px;
      border-color: #e5e7eb;
      font-size: 14px;
      background: #fafafa;
      transition: all 0.2s;
      &:focus {
        border-color: #1a56db;
        background: #fff;
        box-shadow: 0 0 0 3px rgba(26,86,219,0.1);
      }
    }
    .el-input__prefix { line-height: 44px; color: #9ca3af; }
  }

  .mms-code-item .el-form-item__content {
    display: flex;
    gap: 10px;
    align-items: flex-start;
  }

  .mms-code-input { flex: 1; }

  .mms-code-img {
    width: 112px;
    height: 44px;
    flex-shrink: 0;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    border: 1px solid #e5e7eb;
    transition: border-color 0.2s;
    &:hover { border-color: #1a56db; }
    img { width: 100%; height: 100%; object-fit: cover; display: block; }
  }

  .mms-remember {
    margin-bottom: 4px;
    .el-checkbox__label { font-size: 13px; color: #6b7280; }
  }

  .mms-login-btn {
    width: 100%;
    height: 46px;
    font-size: 15px;
    font-weight: 600;
    letter-spacing: 3px;
    border-radius: 8px;
    background: linear-gradient(135deg, #1a56db, #2563eb);
    border: none;
    margin-top: 10px;
    transition: all 0.25s;
    &:hover {
      background: linear-gradient(135deg, #1e40af, #1a56db);
      box-shadow: 0 6px 18px rgba(26,86,219,0.35);
      transform: translateY(-1px);
    }
    &:active { transform: translateY(0); box-shadow: none; }
  }

  .mms-form-footer {
    margin-top: 28px;
    font-size: 12px;
    color: #94a3b8;
    text-align: center;
  }
}
</style>
