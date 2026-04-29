<template>
  <div class="lp-root">
    <!-- 点阵背景 -->
    <div class="lp-grid"></div>

    <div class="lp-card">
      <!-- Logo 区 -->
      <div class="lp-logo-row">
        <div class="lp-logo-icon">
          <svg viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="3" y="6" width="22" height="17" rx="3" stroke="currentColor" stroke-width="1.8"/>
            <path d="M3 11h22" stroke="currentColor" stroke-width="1.8"/>
            <circle cx="8.5" cy="4" r="1.5" fill="currentColor"/>
            <circle cx="19.5" cy="4" r="1.5" fill="currentColor"/>
            <rect x="8" y="15" width="4" height="4" rx="1" fill="currentColor" opacity="0.5"/>
            <rect x="14" y="15" width="4" height="4" rx="1" fill="currentColor"/>
          </svg>
        </div>
        <span class="lp-logo-name">会议管理系统</span>
      </div>

      <!-- 问候 -->
      <div class="lp-greet">
        <span class="lp-time">{{ currentTime }}</span>
        <span class="lp-greet-text">{{ greeting }}，请登录您的账号</span>
      </div>

      <!-- 登录类型切换 -->
      <div class="lp-type-switch">
        <button
          class="lp-type-btn"
          :class="{ active: loginForm.loginType === '1' }"
          @click="switchType('1')">域账号</button>
        <button
          class="lp-type-btn"
          :class="{ active: loginForm.loginType === '0' }"
          @click="switchType('0')">系统账号</button>
      </div>

      <!-- 表单 -->
      <el-form ref="loginForm" :model="loginForm" :rules="currentRules" class="lp-form">
        <el-form-item prop="username">
          <div class="lp-field">
            <label class="lp-label">账号</label>
            <el-input
              v-model="loginForm.username"
              placeholder="请输入账号"
              auto-complete="off"
              size="medium" />
          </div>
        </el-form-item>

        <el-form-item prop="password">
          <div class="lp-field">
            <label class="lp-label">密码</label>
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              auto-complete="off"
              size="medium"
              show-password
              @keyup.enter.native="handleLogin" />
          </div>
        </el-form-item>

        <template v-if="loginForm.loginType === '0'">
          <el-form-item prop="code">
            <div class="lp-field">
              <label class="lp-label">验证码</label>
              <div class="lp-code-row">
                <el-input
                  v-model="loginForm.code"
                  placeholder="请输入验证码"
                  auto-complete="off"
                  size="medium"
                  @keyup.enter.native="handleLogin" />
                <div class="lp-captcha" @click="getCode" title="点击刷新">
                  <img :src="codeUrl" alt="验证码" />
                  <div class="lp-captcha-overlay"><i class="el-icon-refresh"></i></div>
                </div>
              </div>
            </div>
          </el-form-item>

          <div class="lp-remember">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
          </div>
        </template>

        <el-button
          :loading="loading"
          type="primary"
          class="lp-submit"
          @click.native.prevent="handleLogin">
          <span v-if="!loading">登录</span>
          <span v-else>登录中...</span>
        </el-button>
      </el-form>

      <div class="lp-footer">
        <i class="el-icon-lock"></i> 数据加密传输 · 安全访问
      </div>
    </div>

    <p class="lp-copy">© 2024 会议管理系统 · All Rights Reserved</p>
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
        username: [{ required: true, trigger: "blur", message: "请输入域账号" }],
        password: [{ required: true, trigger: "blur", message: "请输入密码" }]
      },
      loading: false,
      redirect: undefined,
      currentTime: "",
      clockTimer: null
    };
  },
  computed: {
    currentRules() {
      return this.loginForm.loginType === "1" ? this.ldapRules : this.sysRules;
    },
    greeting() {
      const h = new Date().getHours();
      if (h < 6)  return "夜深了";
      if (h < 11) return "早上好";
      if (h < 13) return "中午好";
      if (h < 18) return "下午好";
      return "晚上好";
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
    this.tickClock();
    this.clockTimer = setInterval(this.tickClock, 1000);
  },
  beforeDestroy() {
    clearInterval(this.clockTimer);
  },
  methods: {
    tickClock() {
      const now = new Date();
      const p = n => String(n).padStart(2, '0');
      this.currentTime = `${p(now.getHours())}:${p(now.getMinutes())}`;
    },
    switchType(type) {
      this.loginForm.loginType = type;
      this.$nextTick(() => this.$refs.loginForm.clearValidate());
    },
    getCode() {
      getCodeImg().then(res => {
        this.codeUrl = "data:image/gif;base64," + res.img;
        this.loginForm.uuid = res.uuid;
      });
    },
    getCookie() {
      const username   = Cookies.get("username");
      const password   = Cookies.get("password");
      const rememberMe = Cookies.get("rememberMe");
      if (username) {
        this.loginForm.username   = username;
        this.loginForm.password   = password ? decrypt(password) : "";
        this.loginForm.rememberMe = rememberMe === "true";
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return;
        this.loading = true;
        if (this.loginForm.loginType === "0") {
          if (this.loginForm.rememberMe) {
            Cookies.set("username",   this.loginForm.username,          { expires: 30 });
            Cookies.set("password",   encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set("rememberMe", true,                              { expires: 30 });
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
/* ── 重置 ── */
* { box-sizing: border-box; }

/* ── 动画 ── */
@keyframes lp-in {
  from { opacity: 0; transform: translateY(12px) scale(0.98); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

/* ════════════ 根容器 ════════════ */
.lp-root {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f7f8fc;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Helvetica Neue', sans-serif;
  padding: 24px;
  position: relative;
  overflow: hidden;
}

/* 点阵背景 */
.lp-grid {
  position: fixed;
  inset: 0;
  background-image:
    radial-gradient(circle, #d1d5e0 1px, transparent 1px);
  background-size: 28px 28px;
  opacity: 0.55;
  pointer-events: none;
}

/* ════════════ 卡片 ════════════ */
.lp-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 400px;
  background: #ffffff;
  border: 1px solid #e5e7ef;
  border-radius: 14px;
  padding: 36px 36px 28px;
  box-shadow:
    0 1px 2px rgba(0,0,0,0.04),
    0 8px 32px rgba(0,0,0,0.07);
  animation: lp-in 0.45s cubic-bezier(0.22, 1, 0.36, 1) both;
}

/* ── Logo 行 ── */
.lp-logo-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 28px;
}
.lp-logo-icon {
  width: 36px;
  height: 36px;
  border-radius: 9px;
  background: #18181b;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;
  svg { width: 18px; height: 18px; }
}
.lp-logo-name {
  font-size: 15px;
  font-weight: 600;
  color: #18181b;
  letter-spacing: 0.2px;
}

/* ── 问候 ── */
.lp-greet {
  margin-bottom: 24px;
}
.lp-time {
  display: block;
  font-size: 32px;
  font-weight: 200;
  color: #18181b;
  letter-spacing: 3px;
  font-variant-numeric: tabular-nums;
  line-height: 1.1;
  margin-bottom: 4px;
}
.lp-greet-text {
  font-size: 13px;
  color: #71717a;
}

/* ── 类型切换 ── */
.lp-type-switch {
  display: flex;
  gap: 6px;
  margin-bottom: 22px;
  padding: 3px;
  background: #f4f4f5;
  border-radius: 8px;
}
.lp-type-btn {
  flex: 1;
  height: 30px;
  border: none;
  background: transparent;
  border-radius: 6px;
  font-size: 13px;
  color: #71717a;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.18s;
  font-weight: 500;
  &.active {
    background: #ffffff;
    color: #18181b;
    box-shadow: 0 1px 3px rgba(0,0,0,0.1), 0 0 0 1px rgba(0,0,0,0.04);
  }
  &:hover:not(.active) { color: #18181b; }
}

/* ── 表单 ── */
.lp-form {
  .el-form-item {
    margin-bottom: 16px;
  }
  .el-form-item__error {
    font-size: 11.5px;
    padding-top: 3px;
  }
  .el-input__inner {
    height: 40px;
    border-radius: 8px !important;
    border: 1px solid #e4e4e7 !important;
    background: #fafafa !important;
    color: #18181b !important;
    font-size: 14px !important;
    padding-left: 12px !important;
    transition: border-color 0.15s, box-shadow 0.15s !important;
    &:focus {
      border-color: #18181b !important;
      background: #ffffff !important;
      box-shadow: 0 0 0 3px rgba(24,24,27,0.06) !important;
    }
    &::placeholder { color: #a1a1aa !important; }
  }
  .el-input__suffix { line-height: 40px; }
}

.lp-field { display: flex; flex-direction: column; gap: 6px; }
.lp-label {
  font-size: 12.5px;
  font-weight: 500;
  color: #3f3f46;
}

/* 验证码行 */
.lp-code-row {
  display: flex;
  gap: 8px;
  align-items: center;
}
.lp-captcha {
  position: relative;
  width: 100px;
  height: 40px;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e4e4e7;
  cursor: pointer;
  img { width: 100%; height: 100%; object-fit: cover; display: block; }
  &:hover .lp-captcha-overlay { opacity: 1; }
}
.lp-captcha-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.48);
  color: #fff;
  font-size: 16px;
  opacity: 0;
  transition: opacity 0.18s;
}

/* 记住密码 */
.lp-remember {
  margin: -6px 0 14px;
  .el-checkbox__label { font-size: 12.5px; color: #71717a; }
  .el-checkbox__inner {
    border-radius: 4px;
    border-color: #d4d4d8;
  }
}

/* 提交按钮 */
.lp-submit {
  width: 100%;
  height: 40px;
  margin-top: 4px;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.3px;
  border-radius: 8px !important;
  background: #18181b !important;
  border-color: #18181b !important;
  box-shadow: 0 1px 2px rgba(0,0,0,0.12) !important;
  transition: opacity 0.15s, box-shadow 0.15s !important;
  &:hover {
    opacity: 0.88;
    box-shadow: 0 4px 12px rgba(0,0,0,0.2) !important;
  }
  &:active { opacity: 1; }
}

/* 底部安全提示 */
.lp-footer {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f4f4f5;
  text-align: center;
  font-size: 11.5px;
  color: #a1a1aa;
  i { margin-right: 4px; }
}

/* 版权 */
.lp-copy {
  position: relative;
  z-index: 1;
  margin-top: 20px;
  font-size: 11.5px;
  color: #a1a1aa;
  text-align: center;
}
</style>
