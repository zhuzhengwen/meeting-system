<template>
  <div class="lp-root">
    <div class="lp-wrap">

      <!-- ══ 左侧品牌区 ══ -->
      <div class="lp-brand">
        <!-- 背景装饰圆 -->
        <div class="lp-blob lp-blob1"></div>
        <div class="lp-blob lp-blob2"></div>

        <div class="lp-brand-inner">
          <!-- 顶部 logo -->
          <div class="lp-brand-logo">
            <div class="lp-logo-icon">
              <svg viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="6" width="22" height="17" rx="3" stroke="currentColor" stroke-width="1.8"/>
                <path d="M3 11h22" stroke="currentColor" stroke-width="1.8"/>
                <circle cx="8.5" cy="4" r="1.5" fill="currentColor"/>
                <circle cx="19.5" cy="4" r="1.5" fill="currentColor"/>
                <rect x="8" y="15" width="4" height="4" rx="1" fill="currentColor" opacity="0.6"/>
                <rect x="14" y="15" width="4" height="4" rx="1" fill="currentColor"/>
              </svg>
            </div>
            <span class="lp-brand-name">会议管理系统</span>
          </div>

          <!-- 主插图 -->
          <div class="lp-illus-wrap">
            <svg class="lp-illus" viewBox="0 0 360 300" fill="none" xmlns="http://www.w3.org/2000/svg">
              <!-- 会议桌 -->
              <ellipse cx="180" cy="210" rx="130" ry="28" fill="rgba(255,255,255,0.06)" stroke="rgba(255,255,255,0.15)" stroke-width="1.5"/>

              <!-- 主屏幕 -->
              <rect x="90" y="60" width="180" height="115" rx="10" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.2)" stroke-width="1.5"/>
              <rect x="90" y="60" width="180" height="7" rx="3.5" fill="rgba(255,255,255,0.18)"/>
              <!-- 屏幕内容条 -->
              <rect x="108" y="80"  width="100" height="6" rx="3" fill="rgba(255,255,255,0.55)"/>
              <rect x="108" y="93"  width="68"  height="4" rx="2" fill="rgba(255,255,255,0.22)"/>
              <rect x="108" y="106" width="144" height="3" rx="1.5" fill="rgba(255,255,255,0.1)"/>
              <rect x="108" y="113" width="120" height="3" rx="1.5" fill="rgba(255,255,255,0.1)"/>
              <rect x="108" y="120" width="132" height="3" rx="1.5" fill="rgba(255,255,255,0.1)"/>
              <!-- 进度条 -->
              <rect x="108" y="133" width="144" height="6" rx="3" fill="rgba(255,255,255,0.07)"/>
              <rect x="108" y="133" width="88"  height="6" rx="3" fill="rgba(129,202,255,0.6)"/>
              <!-- 屏幕支架 -->
              <line x1="180" y1="175" x2="180" y2="192" stroke="rgba(255,255,255,0.2)" stroke-width="3" stroke-linecap="round"/>
              <rect x="162" y="192" width="36" height="4" rx="2" fill="rgba(255,255,255,0.15)"/>

              <!-- 左侧人物 -->
              <circle cx="82" cy="192" r="16" fill="rgba(99,179,255,0.25)" stroke="rgba(99,179,255,0.5)" stroke-width="1.5"/>
              <circle cx="82" cy="188" r="5.5" fill="rgba(255,255,255,0.45)"/>
              <path d="M70 202 Q82 196 94 202" stroke="rgba(255,255,255,0.35)" stroke-width="1.5" fill="none" stroke-linecap="round"/>
              <!-- 左侧气泡 -->
              <rect x="44" y="162" width="52" height="20" rx="8" fill="rgba(255,255,255,0.1)" stroke="rgba(255,255,255,0.18)" stroke-width="1"/>
              <rect x="52" y="169" width="36" height="3" rx="1.5" fill="rgba(255,255,255,0.4)"/>
              <rect x="52" y="175" width="24" height="3" rx="1.5" fill="rgba(255,255,255,0.2)"/>
              <polygon points="56,182 64,182 60,188" fill="rgba(255,255,255,0.1)"/>

              <!-- 右侧人物 -->
              <circle cx="278" cy="192" r="16" fill="rgba(167,139,250,0.25)" stroke="rgba(167,139,250,0.5)" stroke-width="1.5"/>
              <circle cx="278" cy="188" r="5.5" fill="rgba(255,255,255,0.45)"/>
              <path d="M266 202 Q278 196 290 202" stroke="rgba(255,255,255,0.35)" stroke-width="1.5" fill="none" stroke-linecap="round"/>
              <!-- 右侧气泡 -->
              <rect x="264" y="162" width="52" height="20" rx="8" fill="rgba(255,255,255,0.1)" stroke="rgba(255,255,255,0.18)" stroke-width="1"/>
              <rect x="272" y="169" width="36" height="3" rx="1.5" fill="rgba(255,255,255,0.4)"/>
              <rect x="272" y="175" width="20" height="3" rx="1.5" fill="rgba(255,255,255,0.2)"/>
              <polygon points="284,182 292,182 288,188" fill="rgba(255,255,255,0.1)"/>

              <!-- 正面人物 -->
              <circle cx="180" cy="218" r="18" fill="rgba(52,211,153,0.25)" stroke="rgba(52,211,153,0.5)" stroke-width="1.5"/>
              <circle cx="180" cy="213" r="6" fill="rgba(255,255,255,0.5)"/>
              <path d="M167 226 Q180 219 193 226" stroke="rgba(255,255,255,0.4)" stroke-width="1.5" fill="none" stroke-linecap="round"/>

              <!-- 浮动卡片：日程 -->
              <rect x="18" y="60" width="80" height="60" rx="10" fill="rgba(255,255,255,0.1)" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
              <rect x="18" y="60" width="80" height="4" rx="2" fill="rgba(129,202,255,0.5)"/>
              <rect x="28" y="72" width="28" height="28" rx="6" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
              <line x1="34" y1="72" x2="34" y2="67" stroke="rgba(255,255,255,0.4)" stroke-width="1.5" stroke-linecap="round"/>
              <line x1="50" y1="72" x2="50" y2="67" stroke="rgba(255,255,255,0.4)" stroke-width="1.5" stroke-linecap="round"/>
              <line x1="28" y1="80" x2="56" y2="80" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
              <circle cx="35" cy="88" r="2.5" fill="rgba(129,202,255,0.8)"/>
              <circle cx="42" cy="88" r="2.5" fill="rgba(255,255,255,0.3)"/>
              <circle cx="49" cy="88" r="2.5" fill="rgba(255,255,255,0.3)"/>
              <rect x="64" y="72" width="26" height="4" rx="2" fill="rgba(255,255,255,0.45)"/>
              <rect x="64" y="80" width="20" height="3" rx="1.5" fill="rgba(255,255,255,0.2)"/>
              <rect x="64" y="87" width="23" height="3" rx="1.5" fill="rgba(255,255,255,0.2)"/>
              <rect x="28" y="102" width="62" height="3" rx="1.5" fill="rgba(255,255,255,0.12)"/>
              <rect x="28" y="109" width="48" height="3" rx="1.5" fill="rgba(255,255,255,0.12)"/>

              <!-- 浮动卡片：已确认徽章 -->
              <rect x="262" y="50" width="82" height="32" rx="16" fill="rgba(52,211,153,0.85)" stroke="rgba(52,211,153,0.4)" stroke-width="1"/>
              <circle cx="280" cy="66" r="9" fill="rgba(255,255,255,0.2)"/>
              <path d="M276 66 L279 69 L285 63" stroke="white" stroke-width="1.8" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
              <rect x="295" y="61" width="38" height="5" rx="2.5" fill="rgba(255,255,255,0.85)"/>
              <rect x="295" y="69" width="26" height="3.5" rx="1.75" fill="rgba(255,255,255,0.5)"/>

              <!-- 小装饰点 -->
              <circle cx="52"  cy="44"  r="3.5" fill="rgba(129,202,255,0.5)"/>
              <circle cx="310" cy="110" r="2.5" fill="rgba(167,139,250,0.55)"/>
              <circle cx="320" cy="180" r="2"   fill="rgba(52,211,153,0.5)"/>
              <circle cx="36"  cy="150" r="2"   fill="rgba(129,202,255,0.4)"/>
              <circle cx="340" cy="50"  r="4"   fill="rgba(167,139,250,0.35)"/>
            </svg>
          </div>

          <!-- 标语 -->
          <h1 class="lp-brand-title">高效协作</h1>
          <p class="lp-brand-sub">智能会议室预约 · 多人实时协同 · 无缝沟通体验</p>

          <!-- 特性标签 -->
          <div class="lp-tags">
            <span class="lp-tag"><i class="el-icon-office-building"></i> 会议室预约</span>
            <span class="lp-tag"><i class="el-icon-user"></i> 多人协同</span>
            <span class="lp-tag"><i class="el-icon-bell"></i> 消息通知</span>
          </div>
        </div>
      </div>

      <!-- ══ 右侧表单区 ══ -->
      <div class="lp-panel">
        <div class="lp-panel-inner">
          <!-- 时钟与问候 -->
          <div class="lp-clock">
            <div class="lp-clock-time">{{ currentTime }}</div>
            <div class="lp-clock-sub">{{ currentDate }} · {{ greeting }}</div>
          </div>

          <h2 class="lp-title">欢迎登录</h2>
          <p class="lp-desc">请选择登录方式并填写账号信息</p>

          <!-- 类型切换 -->
          <div class="lp-type-switch">
            <button class="lp-type-btn" :class="{ active: loginForm.loginType === '1' }" @click="switchType('1')">域账号</button>
            <button class="lp-type-btn" :class="{ active: loginForm.loginType === '0' }" @click="switchType('0')">系统账号</button>
          </div>

          <!-- 表单 -->
          <el-form ref="loginForm" :model="loginForm" :rules="currentRules" class="lp-form">
            <el-form-item prop="username">
              <div class="lp-field">
                <label class="lp-label">账号</label>
                <el-input v-model="loginForm.username" placeholder="请输入账号" auto-complete="off" size="medium" />
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <div class="lp-field">
                <label class="lp-label">密码</label>
                <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"
                  auto-complete="off" size="medium" show-password @keyup.enter.native="handleLogin" />
              </div>
            </el-form-item>

            <template v-if="loginForm.loginType === '0'">
              <el-form-item prop="code">
                <div class="lp-field">
                  <label class="lp-label">验证码</label>
                  <div class="lp-code-row">
                    <el-input v-model="loginForm.code" placeholder="请输入验证码"
                      auto-complete="off" size="medium" @keyup.enter.native="handleLogin" />
                    <div class="lp-captcha" @click="getCode" title="点击刷新">
                      <img :src="codeUrl" alt="验证码" />
                      <div class="lp-captcha-mask"><i class="el-icon-refresh"></i></div>
                    </div>
                  </div>
                </div>
              </el-form-item>

              <div class="lp-remember">
                <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
              </div>
            </template>

            <el-button :loading="loading" type="primary" class="lp-submit" @click.native.prevent="handleLogin">
              <span v-if="!loading">登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form>

          <div class="lp-secure"><i class="el-icon-lock"></i> 数据加密传输 · 安全登录保障</div>
        </div>

        <p class="lp-copy">© 2024 会议管理系统 · All Rights Reserved</p>
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
        username: [{ required: true, trigger: "blur", message: "请输入域账号" }],
        password: [{ required: true, trigger: "blur", message: "请输入密码" }]
      },
      loading: false,
      redirect: undefined,
      currentTime: "",
      currentDate: "",
      clockTimer: null
    };
  },
  computed: {
    currentRules() {
      return this.loginForm.loginType === "1" ? this.ldapRules : this.sysRules;
    },
    greeting() {
      const h = new Date().getHours();
      if (h < 6)  return "夜深了，注意休息";
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
      const now  = new Date();
      const p    = n => String(n).padStart(2, '0');
      const days = ['日','一','二','三','四','五','六'];
      this.currentTime = `${p(now.getHours())}:${p(now.getMinutes())}:${p(now.getSeconds())}`;
      this.currentDate = `${now.getMonth()+1}月${now.getDate()}日 周${days[now.getDay()]}`;
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
@keyframes lp-slide-up {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}
@keyframes lp-float {
  0%, 100% { transform: translateY(0);    }
  50%       { transform: translateY(-10px); }
}
@keyframes lp-blob-move {
  0%, 100% { transform: scale(1) translate(0, 0); }
  33%       { transform: scale(1.08) translate(12px, -16px); }
  66%       { transform: scale(0.95) translate(-10px, 10px); }
}

/* ════ 根容器 ════ */
.lp-root {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0f172a;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Helvetica Neue', sans-serif;
  padding: 24px;
}

/* ════ 外层卡片 ════ */
.lp-wrap {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 580px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 32px 80px rgba(0,0,0,0.5), 0 0 0 1px rgba(255,255,255,0.06);
  animation: lp-slide-up 0.5s cubic-bezier(0.22,1,0.36,1) both;
}

/* ════ 左侧品牌区 ════ */
.lp-brand {
  position: relative;
  width: 52%;
  flex-shrink: 0;
  overflow: hidden;
  background: linear-gradient(145deg, #1e3a5f 0%, #1a2f52 40%, #0f2040 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  @media (max-width: 700px) { display: none; }
}

/* 背景模糊光晕 */
.lp-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  pointer-events: none;
  animation: lp-blob-move ease-in-out infinite;
}
.lp-blob1 {
  width: 320px; height: 320px;
  top: -80px; left: -60px;
  background: rgba(59, 130, 246, 0.18);
  animation-duration: 14s;
}
.lp-blob2 {
  width: 260px; height: 260px;
  bottom: -60px; right: -40px;
  background: rgba(139, 92, 246, 0.15);
  animation-duration: 18s;
  animation-delay: -6s;
}

.lp-brand-inner {
  position: relative;
  z-index: 2;
  padding: 48px 44px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}

/* Logo */
.lp-brand-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 40px;
}
.lp-logo-icon {
  width: 34px;
  height: 34px;
  background: rgba(255,255,255,0.12);
  border: 1px solid rgba(255,255,255,0.18);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  svg { width: 18px; height: 18px; }
}
.lp-brand-name {
  font-size: 15px;
  font-weight: 500;
  color: rgba(255,255,255,0.85);
  letter-spacing: 0.5px;
}

/* 插图 */
.lp-illus-wrap {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
}
.lp-illus {
  width: 100%;
  max-width: 340px;
  animation: lp-float 6s ease-in-out infinite;
  filter: drop-shadow(0 20px 40px rgba(0,0,0,0.3));
}

/* 标语 */
.lp-brand-title {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 8px;
  letter-spacing: 1px;
}
.lp-brand-sub {
  font-size: 12.5px;
  color: rgba(255,255,255,0.4);
  margin: 0 0 24px;
  letter-spacing: 0.5px;
  line-height: 1.7;
}

/* 特性标签 */
.lp-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.lp-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 12px;
  border-radius: 20px;
  border: 1px solid rgba(255,255,255,0.12);
  background: rgba(255,255,255,0.06);
  backdrop-filter: blur(8px);
  color: rgba(255,255,255,0.6);
  font-size: 11.5px;
  letter-spacing: 0.3px;
  transition: all 0.2s;
  i { font-size: 11px; color: #60a5fa; }
  &:hover {
    background: rgba(255,255,255,0.1);
    border-color: rgba(255,255,255,0.22);
    color: rgba(255,255,255,0.85);
  }
}

/* ════ 右侧表单区 ════ */
.lp-panel {
  flex: 1;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.lp-panel-inner {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 48px 44px 32px;
  overflow-y: auto;
}

/* 时钟 */
.lp-clock {
  margin-bottom: 28px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f1f5f9;
}
.lp-clock-time {
  font-size: 34px;
  font-weight: 200;
  color: #0f172a;
  letter-spacing: 4px;
  font-variant-numeric: tabular-nums;
  line-height: 1;
  margin-bottom: 6px;
}
.lp-clock-sub {
  font-size: 12px;
  color: #94a3b8;
  letter-spacing: 0.3px;
}

/* 标题 */
.lp-title {
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 6px;
  letter-spacing: 0.2px;
}
.lp-desc {
  font-size: 13px;
  color: #94a3b8;
  margin: 0 0 24px;
}

/* 类型切换 */
.lp-type-switch {
  display: flex;
  gap: 6px;
  margin-bottom: 24px;
  padding: 3px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
}
.lp-type-btn {
  flex: 1;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 7px;
  font-size: 13px;
  color: #94a3b8;
  cursor: pointer;
  font-family: inherit;
  font-weight: 500;
  transition: all 0.18s;
  &.active {
    background: #ffffff;
    color: #0f172a;
    box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  }
  &:hover:not(.active) { color: #475569; }
}

/* 表单 */
.lp-form {
  .el-form-item { margin-bottom: 18px; }
  .el-form-item__error { font-size: 11.5px; padding-top: 3px; }
  .el-input__inner {
    height: 42px !important;
    border-radius: 9px !important;
    border: 1.5px solid #e2e8f0 !important;
    background: #f8fafc !important;
    color: #0f172a !important;
    font-size: 14px !important;
    padding-left: 12px !important;
    transition: border-color 0.15s, box-shadow 0.15s, background 0.15s !important;
    &:focus {
      border-color: #3b82f6 !important;
      background: #ffffff !important;
      box-shadow: 0 0 0 3px rgba(59,130,246,0.1) !important;
    }
    &::placeholder { color: #cbd5e1 !important; }
  }
  .el-input__suffix { line-height: 42px; }
}
.lp-field { display: flex; flex-direction: column; gap: 6px; }
.lp-label { font-size: 12.5px; font-weight: 600; color: #334155; letter-spacing: 0.2px; }

/* 验证码 */
.lp-code-row { display: flex; gap: 8px; align-items: center; }
.lp-captcha {
  position: relative;
  width: 106px;
  height: 42px;
  flex-shrink: 0;
  border-radius: 9px;
  overflow: hidden;
  border: 1.5px solid #e2e8f0;
  cursor: pointer;
  transition: border-color 0.15s;
  &:hover { border-color: #3b82f6; .lp-captcha-mask { opacity: 1; } }
  img { width: 100%; height: 100%; object-fit: cover; display: block; }
}
.lp-captcha-mask {
  position: absolute; inset: 0;
  display: flex; align-items: center; justify-content: center;
  background: rgba(15,23,42,0.5);
  color: #fff; font-size: 18px;
  opacity: 0; transition: opacity 0.18s;
}

/* 记住密码 */
.lp-remember {
  margin: -4px 0 14px;
  .el-checkbox__label { font-size: 12.5px; color: #94a3b8; }
  .el-checkbox__inner { border-radius: 4px; border-color: #cbd5e1; }
}

/* 提交按钮 */
.lp-submit {
  width: 100%;
  height: 42px;
  margin-top: 4px;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.5px;
  border-radius: 9px !important;
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%) !important;
  border: none !important;
  box-shadow: 0 2px 8px rgba(59,130,246,0.3), 0 1px 2px rgba(0,0,0,0.08) !important;
  transition: all 0.2s !important;
  &:hover {
    box-shadow: 0 4px 16px rgba(59,130,246,0.45), 0 1px 4px rgba(0,0,0,0.1) !important;
    transform: translateY(-1px);
  }
  &:active { transform: translateY(0); }
}

/* 安全提示 */
.lp-secure {
  margin-top: 20px;
  text-align: center;
  font-size: 11.5px;
  color: #cbd5e1;
  i { margin-right: 4px; }
}

/* 版权 */
.lp-copy {
  padding: 16px 44px;
  font-size: 11px;
  color: #cbd5e1;
  text-align: center;
  border-top: 1px solid #f1f5f9;
}
</style>
