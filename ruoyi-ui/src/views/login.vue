<template>
  <div class="lp-root">

    <!-- 左侧品牌区 -->
    <div class="lp-brand">
      <!-- 动态粒子 -->
      <div class="lp-particles">
        <span v-for="i in 20" :key="i" class="lp-dot" :style="dotStyle(i)"></span>
      </div>

      <div class="lp-brand-body">
        <!-- 带旋转动画的线条插图 -->
        <div class="lp-illus-wrap">
          <svg class="lp-illus" viewBox="0 0 300 300" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <radialGradient id="tableGrad" cx="50%" cy="50%" r="50%">
                <stop offset="0%" stop-color="rgba(147,197,253,0.18)"/>
                <stop offset="100%" stop-color="rgba(147,197,253,0)"/>
              </radialGradient>
              <filter id="glow">
                <feGaussianBlur stdDeviation="1.5" result="blur"/>
                <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
              </filter>
            </defs>
            <!-- 同心环 -->
            <circle cx="150" cy="150" r="140" stroke="rgba(255,255,255,0.06)" fill="none" stroke-width="0.6"/>
            <circle cx="150" cy="150" r="115" stroke="rgba(255,255,255,0.08)" fill="none" stroke-width="0.6"/>
            <circle cx="150" cy="150" r="88"  stroke="rgba(255,255,255,0.10)" fill="none" stroke-width="0.6"/>
            <!-- 旋转方框 -->
            <rect x="16"  y="16"  width="268" height="268" fill="none" stroke="rgba(255,255,255,0.06)" stroke-width="0.5" transform="rotate(45 150 150)"/>
            <rect x="55"  y="55"  width="190" height="190" fill="none" stroke="rgba(255,255,255,0.07)" stroke-width="0.5" transform="rotate(45 150 150)"/>
            <!-- 十字轴线 -->
            <line x1="0"   y1="150" x2="300" y2="150" stroke="rgba(255,255,255,0.05)" stroke-width="0.6"/>
            <line x1="150" y1="0"   x2="150" y2="300" stroke="rgba(255,255,255,0.05)" stroke-width="0.6"/>
            <!-- 虚线内圆 -->
            <circle cx="150" cy="150" r="66" stroke="rgba(147,197,253,0.35)" fill="none" stroke-width="1" stroke-dasharray="5 7"/>
            <!-- 会议桌填充 + 描边 -->
            <ellipse cx="150" cy="153" rx="43" ry="25" fill="url(#tableGrad)"/>
            <ellipse cx="150" cy="153" rx="43" ry="25" fill="none" stroke="rgba(147,197,253,0.8)" stroke-width="1.5"/>
            <!-- 6位参会人 -->
            <circle cx="150" cy="107" r="9.5" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.55)" stroke-width="1.5"/>
            <circle cx="192" cy="121" r="9.5" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.55)" stroke-width="1.5"/>
            <circle cx="192" cy="185" r="9.5" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.55)" stroke-width="1.5"/>
            <circle cx="150" cy="199" r="9.5" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.55)" stroke-width="1.5"/>
            <circle cx="108" cy="185" r="9.5" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.55)" stroke-width="1.5"/>
            <circle cx="108" cy="121" r="9.5" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.55)" stroke-width="1.5"/>
            <!-- 连线 -->
            <line x1="150" y1="117" x2="150" y2="128" stroke="rgba(147,197,253,0.45)" stroke-width="1"/>
            <line x1="184" y1="127" x2="178" y2="134" stroke="rgba(147,197,253,0.45)" stroke-width="1"/>
            <line x1="184" y1="179" x2="178" y2="172" stroke="rgba(147,197,253,0.45)" stroke-width="1"/>
            <line x1="150" y1="189" x2="150" y2="178" stroke="rgba(147,197,253,0.45)" stroke-width="1"/>
            <line x1="116" y1="179" x2="122" y2="172" stroke="rgba(147,197,253,0.45)" stroke-width="1"/>
            <line x1="116" y1="127" x2="122" y2="134" stroke="rgba(147,197,253,0.45)" stroke-width="1"/>
            <!-- 亮蓝点（发光） -->
            <circle cx="150" cy="107" r="3.5" fill="#7dd3fc" filter="url(#glow)"/>
            <circle cx="192" cy="121" r="3.5" fill="#7dd3fc" filter="url(#glow)"/>
            <circle cx="192" cy="185" r="3.5" fill="#7dd3fc" filter="url(#glow)"/>
            <circle cx="150" cy="199" r="3.5" fill="#7dd3fc" filter="url(#glow)"/>
            <circle cx="108" cy="185" r="3.5" fill="#7dd3fc" filter="url(#glow)"/>
            <circle cx="108" cy="121" r="3.5" fill="#7dd3fc" filter="url(#glow)"/>
            <!-- 角落标记 -->
            <path d="M18,18 L18,32 M18,18 L32,18"   stroke="rgba(147,197,253,0.4)" stroke-width="1.2" fill="none" stroke-linecap="square"/>
            <path d="M282,18 L282,32 M282,18 L268,18" stroke="rgba(147,197,253,0.4)" stroke-width="1.2" fill="none" stroke-linecap="square"/>
            <path d="M18,282 L18,268 M18,282 L32,282" stroke="rgba(147,197,253,0.4)" stroke-width="1.2" fill="none" stroke-linecap="square"/>
            <path d="M282,282 L282,268 M282,282 L268,282" stroke="rgba(147,197,253,0.4)" stroke-width="1.2" fill="none" stroke-linecap="square"/>
          </svg>
          <!-- 外旋转环 -->
          <div class="lp-spin-ring lp-spin-cw"></div>
          <div class="lp-spin-ring lp-spin-ccw"></div>
        </div>

        <div class="lp-rule"></div>
        <h1 class="lp-brand-title">会议管理系统</h1>
        <p class="lp-brand-en">MEETING MANAGEMENT SYSTEM</p>
        <div class="lp-rule"></div>

        <!-- 统计数字 -->
        <div class="lp-stats">
          <div class="lp-stat">
            <span class="lp-stat-n">50<sup>+</sup></span>
            <span class="lp-stat-l">会议室</span>
          </div>
          <div class="lp-stat-sep"></div>
          <div class="lp-stat">
            <span class="lp-stat-n">1K<sup>+</sup></span>
            <span class="lp-stat-l">注册用户</span>
          </div>
          <div class="lp-stat-sep"></div>
          <div class="lp-stat">
            <span class="lp-stat-n">24<sup>/7</sup></span>
            <span class="lp-stat-l">全天候</span>
          </div>
        </div>

        <ul class="lp-feats">
          <li>统一会议预约与排程</li>
          <li>智能会议室资源管理</li>
          <li>精细化部门数据权限</li>
        </ul>
      </div>

      <div class="lp-bgc lp-bgc1"></div>
      <div class="lp-bgc lp-bgc2"></div>
    </div>

    <!-- 右侧表单区 -->
    <div class="lp-panel">
      <!-- 背景气泡 -->
      <div class="lp-blob lp-blob1"></div>
      <div class="lp-blob lp-blob2"></div>

      <div class="lp-card">
        <div class="lp-card-bar"></div>
        <div class="lp-card-body">
          <!-- 时钟与问候 -->
          <div class="lp-clock-wrap">
            <div class="lp-clock-time">{{ currentTime }}</div>
            <div class="lp-clock-meta">
              <span class="lp-clock-date">{{ currentDate }}</span>
              <span class="lp-clock-sep">·</span>
              <span class="lp-clock-greet">{{ greeting }}</span>
            </div>
          </div>

          <h2 class="lp-card-title">欢迎登录</h2>
          <p class="lp-card-desc">会议管理系统 · 请填写您的账号信息</p>

          <el-tabs v-model="loginForm.loginType" class="lp-tabs" @tab-click="onTabChange">
            <el-tab-pane label="域账号"  name="1" />
            <el-tab-pane label="系统账号" name="0" />
          </el-tabs>

          <el-form ref="loginForm" :model="loginForm" :rules="currentRules" class="lp-form">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" type="text" auto-complete="off"
                placeholder="账号" prefix-icon="el-icon-user" size="medium" />
            </el-form-item>

            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" auto-complete="off"
                placeholder="密码" prefix-icon="el-icon-lock" size="medium" show-password
                @keyup.enter.native="handleLogin" />
            </el-form-item>

            <el-form-item v-if="loginForm.loginType === '0'" prop="code" class="lp-code-row">
              <el-input v-model="loginForm.code" auto-complete="off" placeholder="验证码"
                prefix-icon="el-icon-key" size="medium" class="lp-code-input"
                @keyup.enter.native="handleLogin" />
              <div class="lp-captcha" @click="getCode" title="点击刷新验证码">
                <img :src="codeUrl" alt="验证码" />
                <div class="lp-captcha-mask">
                  <i class="el-icon-refresh"></i>
                  <span>刷新</span>
                </div>
              </div>
            </el-form-item>

            <div v-if="loginForm.loginType === '0'" class="lp-remember">
              <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
            </div>

            <el-button :loading="loading" type="primary" class="lp-btn"
              @click.native.prevent="handleLogin">
              <span v-if="!loading">登&nbsp;&nbsp;录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form>

          <div class="lp-secure">
            <i class="el-icon-lock"></i> 数据加密传输 &nbsp;·&nbsp; 安全登录保障
          </div>
        </div>
      </div>
      <p class="lp-copy">Copyright © 2024 会议管理系统 &nbsp;|&nbsp; All Rights Reserved</p>
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
      const now = new Date();
      const p = n => String(n).padStart(2, '0');
      this.currentTime = `${p(now.getHours())}:${p(now.getMinutes())}:${p(now.getSeconds())}`;
      const days = ['日','一','二','三','四','五','六'];
      this.currentDate = `${now.getMonth()+1}月${now.getDate()}日 周${days[now.getDay()]}`;
    },
    dotStyle(i) {
      const size  = 2 + (i % 4) * 1.5;
      const left  = ((i * 17 + 9) % 88) + 6;
      const top   = ((i * 23 + 5) % 84) + 8;
      const delay = (i * 0.55) % 7;
      const dur   = 7 + (i % 5) * 1.8;
      return {
        width:  `${size}px`,
        height: `${size}px`,
        left:   `${left}%`,
        top:    `${top}%`,
        animationDelay:    `${delay}s`,
        animationDuration: `${dur}s`
      };
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
    onTabChange() {
      this.$nextTick(() => this.$refs.loginForm.clearValidate());
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
/* ── 全局动画 ── */
@keyframes lp-float {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.25; }
  50%       { transform: translateY(-24px) scale(1.4); opacity: 0.55; }
}
@keyframes lp-spin-cw  { to { transform: rotate(360deg);  } }
@keyframes lp-spin-ccw { to { transform: rotate(-360deg); } }
@keyframes lp-pulse {
  0%, 100% { opacity: 0.6; transform: scale(1); }
  50%       { opacity: 1;   transform: scale(1.04); }
}
@keyframes lp-slide-up {
  from { opacity: 0; transform: translateY(18px); }
  to   { opacity: 1; transform: translateY(0); }
}
@keyframes lp-blob {
  0%, 100% { transform: translate(0, 0) scale(1); }
  40%       { transform: translate(18px, -12px) scale(1.06); }
  70%       { transform: translate(-10px, 8px)  scale(0.95); }
}
@keyframes lp-bar-shine {
  0%   { background-position: -200% center; }
  100% { background-position:  200% center; }
}

.lp-root {
  display: flex;
  height: 100vh;
  min-height: 560px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', sans-serif;

  /* ════════════ 左侧品牌区 ════════════ */
  .lp-brand {
    position: relative;
    width: 46%;
    flex-shrink: 0;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    background:
      linear-gradient(rgba(255,255,255,0.04) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255,255,255,0.04) 1px, transparent 1px),
      linear-gradient(160deg, #0d2260 0%, #163799 45%, #1d50c8 100%);
    background-size: 50px 50px, 50px 50px, cover;
    @media (max-width: 768px) { display: none; }
  }

  /* 粒子 */
  .lp-particles { position: absolute; inset: 0; pointer-events: none; }
  .lp-dot {
    position: absolute;
    border-radius: 50%;
    background: rgba(96,165,250,0.7);
    animation: lp-float ease-in-out infinite;
  }

  .lp-brand-body {
    position: relative;
    z-index: 2;
    text-align: center;
    padding: 40px 48px;
    animation: lp-slide-up 0.7s ease both;
  }

  /* 插图外旋转环 */
  .lp-illus-wrap {
    position: relative;
    width: 240px;
    height: 240px;
    margin: 0 auto 10px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .lp-spin-ring {
    position: absolute;
    border-radius: 50%;
    pointer-events: none;
  }

  .lp-spin-cw {
    width: 228px; height: 228px;
    border: 1px dashed rgba(147,197,253,0.3);
    border-top-color: rgba(125,211,252,0.75);
    animation: lp-spin-cw 16s linear infinite;
  }

  .lp-spin-ccw {
    width: 200px; height: 200px;
    border: 1px dashed rgba(147,197,253,0.2);
    border-right-color: rgba(147,197,253,0.55);
    animation: lp-spin-ccw 10s linear infinite;
  }

  .lp-illus {
    width: 180px;
    height: 180px;
    display: block;
    animation: lp-pulse 4s ease-in-out infinite;
  }

  .lp-rule {
    width: 100%;
    height: 1px;
    background: linear-gradient(90deg,
      transparent 0%,
      rgba(255,255,255,0.18) 25%,
      rgba(255,255,255,0.18) 75%,
      transparent 100%
    );
    margin: 14px 0;
  }

  .lp-brand-title {
    color: #fff;
    font-size: 30px;
    font-weight: 400;
    letter-spacing: 8px;
    margin: 0;
  }

  .lp-brand-en {
    color: rgba(255,255,255,0.22);
    font-size: 9px;
    letter-spacing: 3px;
    margin: 8px 0 0;
  }

  /* 统计数字 */
  .lp-stats {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 16px 0 4px;
    gap: 0;
  }

  .lp-stat {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 0 18px;
  }

  .lp-stat-n {
    font-size: 20px;
    font-weight: 600;
    color: #fff;
    line-height: 1.1;
    sup {
      font-size: 11px;
      font-weight: 400;
      color: #60a5fa;
      vertical-align: super;
    }
  }

  .lp-stat-l {
    font-size: 10px;
    color: rgba(255,255,255,0.38);
    margin-top: 3px;
    letter-spacing: 1px;
  }

  .lp-stat-sep {
    width: 1px;
    height: 28px;
    background: rgba(255,255,255,0.1);
  }

  .lp-feats {
    list-style: none;
    padding: 0;
    margin: 0;
    display: inline-block;
    text-align: left;

    li {
      position: relative;
      padding-left: 20px;
      color: rgba(255,255,255,0.55);
      font-size: 12.5px;
      letter-spacing: 0.5px;
      margin-bottom: 10px;
      transition: color 0.2s;
      &:hover { color: rgba(255,255,255,0.85); }

      &::before {
        content: '—';
        position: absolute;
        left: 0;
        color: #60a5fa;
        font-size: 11px;
        line-height: inherit;
      }

      &:last-child { margin-bottom: 0; }
    }
  }

  .lp-bgc {
    position: absolute;
    border-radius: 50%;
    border: 1px solid rgba(255,255,255,0.04);
    pointer-events: none;
  }
  .lp-bgc1 { width: 540px; height: 540px; top: -230px; right: -190px; }
  .lp-bgc2 { width: 320px; height: 320px; bottom: -130px; left: -110px; }

  /* ════════════ 右侧表单区 ════════════ */
  .lp-panel {
    flex: 1;
    background: #edf1fb;
    background-image: radial-gradient(rgba(26,86,219,0.08) 1px, transparent 1px);
    background-size: 26px 26px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 24px;
    position: relative;
    overflow: hidden;
  }

  /* 背景气泡 */
  .lp-blob {
    position: absolute;
    border-radius: 50%;
    filter: blur(70px);
    pointer-events: none;
  }
  .lp-blob1 {
    width: 320px; height: 320px;
    background: rgba(59,130,246,0.1);
    top: -100px; right: -80px;
    animation: lp-blob 14s ease-in-out infinite;
  }
  .lp-blob2 {
    width: 280px; height: 280px;
    background: rgba(99,102,241,0.08);
    bottom: -80px; left: -60px;
    animation: lp-blob 18s ease-in-out infinite reverse;
  }

  .lp-card {
    width: 100%;
    max-width: 400px;
    background: rgba(255,255,255,0.96);
    border: 1px solid rgba(210,218,240,0.8);
    border-radius: 4px;
    overflow: hidden;
    box-shadow:
      0 2px 8px rgba(26,86,219,0.06),
      0 8px 32px rgba(26,86,219,0.10),
      0 1px 0 rgba(255,255,255,0.9) inset;
    position: relative;
    z-index: 1;
    animation: lp-slide-up 0.6s ease 0.08s both;
  }

  /* 顶部彩条 —— 流光效果 */
  .lp-card-bar {
    height: 3px;
    background: linear-gradient(90deg,
      #1a56db 0%, #60a5fa 40%, #a5f3fc 55%, #60a5fa 70%, #1a56db 100%
    );
    background-size: 200% auto;
    animation: lp-bar-shine 3s linear infinite;
  }

  .lp-card-body {
    padding: 28px 40px 32px;
  }

  /* 实时时钟 */
  .lp-clock-wrap {
    text-align: center;
    margin-bottom: 18px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f4ff;
  }

  .lp-clock-time {
    font-size: 32px;
    font-weight: 200;
    color: #1a56db;
    letter-spacing: 4px;
    font-variant-numeric: tabular-nums;
    line-height: 1;
  }

  .lp-clock-meta {
    margin-top: 5px;
    font-size: 11px;
    color: #b0bcd0;
    letter-spacing: 0.5px;
  }

  .lp-clock-sep { margin: 0 5px; }

  .lp-clock-greet { color: #1a56db; }

  .lp-card-title {
    font-size: 20px;
    font-weight: 600;
    color: #111827;
    margin: 0 0 5px;
    letter-spacing: 0.5px;
  }

  .lp-card-desc {
    font-size: 12px;
    color: #9ca3af;
    margin: 0 0 22px;
    letter-spacing: 0.3px;
  }

  /* tabs */
  .lp-tabs {
    margin-bottom: 22px;
    .el-tabs__header { margin-bottom: 0; }
    .el-tabs__nav-wrap::after { height: 1px; background: #f0f4ff; }
    .el-tabs__item {
      font-size: 13px;
      color: #9ca3af;
      padding: 0 16px;
      height: 34px;
      line-height: 34px;
      &.is-active { color: #1a56db; font-weight: 600; }
    }
    .el-tabs__active-bar { background: #1a56db; height: 2px; }
  }

  /* 下划线风格输入框 */
  .lp-form {
    .el-form-item { margin-bottom: 22px; }

    .el-input__inner {
      border-top: none !important;
      border-left: none !important;
      border-right: none !important;
      border-bottom: 1.5px solid #dde3ef !important;
      border-radius: 0 !important;
      background: transparent !important;
      box-shadow: none !important;
      padding-left: 32px;
      height: 44px;
      line-height: 44px;
      font-size: 14px;
      color: #111827;
      transition: border-color .2s !important;
    }

    .el-input__inner:focus {
      border-bottom-color: #1a56db !important;
      background: transparent !important;
      box-shadow: none !important;
    }

    .el-input__prefix { line-height: 44px; color: #b0bcd0; }
    .el-input__suffix { line-height: 44px; }
    .el-form-item__error { padding-top: 4px; font-size: 11px; }
  }

  .lp-code-row .el-form-item__content {
    display: flex;
    align-items: center;
  }
  .lp-code-input { flex: 1; min-width: 0; }
  .lp-captcha {
    position: relative;
    width: 108px;
    height: 44px;
    flex-shrink: 0;
    margin-left: 10px;
    border: 1.5px solid #d0d9f0;
    border-radius: 6px;
    cursor: pointer;
    overflow: hidden;
    transition: border-color .2s, box-shadow .2s;
    &:hover {
      border-color: #1a56db;
      box-shadow: 0 0 0 3px rgba(26,86,219,0.1);
      .lp-captcha-mask { opacity: 1; }
    }
    img { width: 100%; height: 100%; object-fit: cover; display: block; }
  }

  .lp-captcha-mask {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 2px;
    background: rgba(26,86,219,0.72);
    opacity: 0;
    transition: opacity .2s;
    color: #fff;
    i { font-size: 14px; }
    span { font-size: 10px; letter-spacing: 0.5px; }
  }

  .lp-remember {
    margin-top: -8px;
    margin-bottom: 4px;
    .el-checkbox__label { font-size: 12px; color: #9ca3af; }
  }

  .lp-btn {
    width: 100%;
    height: 44px;
    font-size: 14px;
    font-weight: 600;
    letter-spacing: 4px;
    border-radius: 2px !important;
    margin-top: 10px;
    background: linear-gradient(135deg, #1a56db 0%, #3b82f6 100%) !important;
    border: none !important;
    box-shadow: 0 4px 14px rgba(26,86,219,0.28) !important;
    transition: all 0.25s !important;
    &:hover {
      background: linear-gradient(135deg, #1e40af 0%, #1a56db 100%) !important;
      box-shadow: 0 6px 20px rgba(26,86,219,0.42) !important;
      transform: translateY(-1px);
    }
    &:active { transform: translateY(0); box-shadow: 0 2px 6px rgba(26,86,219,0.2) !important; }
  }

  /* 安全提示 */
  .lp-secure {
    margin-top: 18px;
    text-align: center;
    font-size: 11px;
    color: #c0c8d8;
    i { margin-right: 4px; }
  }

  .lp-copy {
    margin-top: 22px;
    font-size: 11px;
    color: #94a3b8;
    letter-spacing: 0.5px;
    position: relative;
    z-index: 1;
  }
}
</style>
