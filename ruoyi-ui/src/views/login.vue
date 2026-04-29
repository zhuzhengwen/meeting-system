<template>
  <div class="lp-root">

    <!-- ══ 左侧品牌区 ══ -->
    <div class="lp-brand">
      <div class="lp-brand-content">
        <!-- Logo -->
        <div class="lp-logo">
          <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="2" y="7" width="28" height="21" rx="4" stroke="white" stroke-width="1.8"/>
            <path d="M2 13h28" stroke="white" stroke-width="1.8"/>
            <circle cx="10" cy="5" r="2" fill="white"/>
            <circle cx="22" cy="5" r="2" fill="white"/>
            <rect x="9"  y="18" width="5" height="5" rx="1.5" fill="rgba(255,255,255,0.9)"/>
            <rect x="18" y="18" width="5" height="5" rx="1.5" fill="rgba(255,255,255,0.5)"/>
          </svg>
          <span>会议管理系统</span>
        </div>

        <!-- 主标题 -->
        <div class="lp-headline">
          <h1>高效会议<br/>从这里开始</h1>
          <p>统一管理会议室预约、日程协同与消息通知，让每一次会议都有迹可循。</p>
        </div>

        <!-- 核心插图 -->
        <div class="lp-illus">
          <svg viewBox="0 0 420 280" fill="none" xmlns="http://www.w3.org/2000/svg">
            <!-- 网格背景 -->
            <defs>
              <pattern id="grid" width="28" height="28" patternUnits="userSpaceOnUse">
                <path d="M 28 0 L 0 0 0 28" fill="none" stroke="rgba(255,255,255,0.05)" stroke-width="1"/>
              </pattern>
              <linearGradient id="cardGrad" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stop-color="rgba(255,255,255,0.14)"/>
                <stop offset="100%" stop-color="rgba(255,255,255,0.05)"/>
              </linearGradient>
              <linearGradient id="barBlue" x1="0" y1="0" x2="1" y2="0">
                <stop offset="0%" stop-color="#60a5fa"/>
                <stop offset="100%" stop-color="#93c5fd"/>
              </linearGradient>
              <linearGradient id="barGreen" x1="0" y1="0" x2="1" y2="0">
                <stop offset="0%" stop-color="#34d399"/>
                <stop offset="100%" stop-color="#6ee7b7"/>
              </linearGradient>
              <linearGradient id="barPurple" x1="0" y1="0" x2="1" y2="0">
                <stop offset="0%" stop-color="#a78bfa"/>
                <stop offset="100%" stop-color="#c4b5fd"/>
              </linearGradient>
              <filter id="cardShadow">
                <feDropShadow dx="0" dy="8" stdDeviation="16" flood-color="rgba(0,0,0,0.3)"/>
              </filter>
            </defs>
            <rect width="420" height="280" fill="url(#grid)"/>

            <!-- 主卡片 -->
            <rect x="60" y="30" width="210" height="200" rx="14" fill="url(#cardGrad)" stroke="rgba(255,255,255,0.15)" stroke-width="1" filter="url(#cardShadow)"/>
            <!-- 卡片顶色条 -->
            <rect x="60" y="30" width="210" height="4" rx="2" fill="url(#barBlue)"/>

            <!-- 标题行 -->
            <rect x="80" y="52" width="120" height="8" rx="4" fill="rgba(255,255,255,0.75)"/>
            <rect x="80" y="65" width="80"  height="5" rx="2.5" fill="rgba(255,255,255,0.3)"/>

            <!-- 分隔线 -->
            <line x1="80" y1="82" x2="250" y2="82" stroke="rgba(255,255,255,0.08)" stroke-width="1"/>

            <!-- 会议室使用率条形图 -->
            <text x="80" y="100" font-size="9" fill="rgba(255,255,255,0.45)" font-family="sans-serif">会议室使用率</text>

            <rect x="80" y="108" width="150" height="7" rx="3.5" fill="rgba(255,255,255,0.07)"/>
            <rect x="80" y="108" width="105" height="7" rx="3.5" fill="url(#barBlue)"/>
            <text x="238" y="115" font-size="8" fill="rgba(255,255,255,0.5)" font-family="sans-serif">70%</text>

            <rect x="80" y="121" width="150" height="7" rx="3.5" fill="rgba(255,255,255,0.07)"/>
            <rect x="80" y="121" width="135" height="7" rx="3.5" fill="url(#barGreen)"/>
            <text x="238" y="128" font-size="8" fill="rgba(255,255,255,0.5)" font-family="sans-serif">90%</text>

            <rect x="80" y="134" width="150" height="7" rx="3.5" fill="rgba(255,255,255,0.07)"/>
            <rect x="80" y="134" width="75"  height="7" rx="3.5" fill="url(#barPurple)"/>
            <text x="238" y="141" font-size="8" fill="rgba(255,255,255,0.5)" font-family="sans-serif">50%</text>

            <!-- 分隔线 -->
            <line x1="80" y1="153" x2="250" y2="153" stroke="rgba(255,255,255,0.08)" stroke-width="1"/>

            <!-- 今日会议 -->
            <text x="80" y="170" font-size="9" fill="rgba(255,255,255,0.45)" font-family="sans-serif">今日待参加</text>
            <circle cx="88"  cy="187" r="7" fill="rgba(96,165,250,0.3)"  stroke="rgba(96,165,250,0.6)"  stroke-width="1.2"/>
            <circle cx="104" cy="187" r="7" fill="rgba(167,139,250,0.3)" stroke="rgba(167,139,250,0.6)" stroke-width="1.2"/>
            <circle cx="120" cy="187" r="7" fill="rgba(52,211,153,0.3)"  stroke="rgba(52,211,153,0.6)"  stroke-width="1.2"/>
            <circle cx="136" cy="187" r="7" fill="rgba(251,191,36,0.3)"  stroke="rgba(251,191,36,0.6)"  stroke-width="1.2"/>
            <rect x="152" y="180" width="68" height="14" rx="7" fill="rgba(52,211,153,0.2)" stroke="rgba(52,211,153,0.4)" stroke-width="1"/>
            <text x="186" y="190" text-anchor="middle" font-size="8" fill="rgba(52,211,153,0.9)" font-family="sans-serif">3 场待开始</text>

            <!-- 浮动小卡片 1：已确认 -->
            <rect x="252" y="46" width="116" height="48" rx="10" fill="rgba(255,255,255,0.12)" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
            <circle cx="270" cy="62" r="10" fill="rgba(52,211,153,0.3)" stroke="rgba(52,211,153,0.5)" stroke-width="1"/>
            <path d="M265 62 L268.5 65.5 L276 58" stroke="rgba(52,211,153,0.9)" stroke-width="1.6" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
            <rect x="286" y="56" width="70" height="6" rx="3" fill="rgba(255,255,255,0.6)"/>
            <rect x="286" y="66" width="50" height="4" rx="2" fill="rgba(255,255,255,0.25)"/>
            <rect x="260" y="80" width="98" height="4" rx="2" fill="rgba(255,255,255,0.1)"/>

            <!-- 浮动小卡片 2：提醒 -->
            <rect x="252" y="112" width="116" height="48" rx="10" fill="rgba(255,255,255,0.12)" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
            <circle cx="270" cy="128" r="10" fill="rgba(251,191,36,0.3)" stroke="rgba(251,191,36,0.5)" stroke-width="1"/>
            <text x="270" y="132" text-anchor="middle" font-size="11" fill="rgba(251,191,36,0.9)" font-family="sans-serif">!</text>
            <rect x="286" y="122" width="60" height="6" rx="3" fill="rgba(255,255,255,0.6)"/>
            <rect x="286" y="132" width="44" height="4" rx="2" fill="rgba(255,255,255,0.25)"/>
            <rect x="260" y="146" width="98" height="4" rx="2" fill="rgba(255,255,255,0.1)"/>

            <!-- 浮动小卡片 3：统计 -->
            <rect x="252" y="178" width="116" height="72" rx="10" fill="rgba(255,255,255,0.12)" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
            <text x="270" y="200" font-size="9" fill="rgba(255,255,255,0.4)" font-family="sans-serif">本月预约</text>
            <text x="270" y="218" font-size="22" font-weight="bold" fill="rgba(255,255,255,0.85)" font-family="sans-serif">128</text>
            <rect x="270" y="228" width="32" height="14" rx="7" fill="rgba(52,211,153,0.2)"/>
            <text x="286" y="238" text-anchor="middle" font-size="8" fill="rgba(52,211,153,0.9)" font-family="sans-serif">↑12%</text>

            <!-- 装饰光点 -->
            <circle cx="40"  cy="40"  r="4" fill="rgba(96,165,250,0.4)"/>
            <circle cx="400" cy="260" r="3" fill="rgba(167,139,250,0.4)"/>
            <circle cx="35"  cy="240" r="2.5" fill="rgba(52,211,153,0.35)"/>
            <circle cx="395" cy="40"  r="2"   fill="rgba(251,191,36,0.4)"/>
          </svg>
        </div>

        <!-- 底部统计 -->
        <div class="lp-stats">
          <div class="lp-stat">
            <span class="lp-stat-n">128</span>
            <span class="lp-stat-l">本月预约</span>
          </div>
          <div class="lp-stat-div"></div>
          <div class="lp-stat">
            <span class="lp-stat-n">6</span>
            <span class="lp-stat-l">会议室</span>
          </div>
          <div class="lp-stat-div"></div>
          <div class="lp-stat">
            <span class="lp-stat-n">98%</span>
            <span class="lp-stat-l">准时率</span>
          </div>
        </div>
      </div>
    </div>

    <!-- ══ 右侧登录区 ══ -->
    <div class="lp-panel">
      <div class="lp-panel-inner">

        <!-- 时钟 -->
        <div class="lp-clock">
          <div class="lp-clock-time">{{ currentTime }}</div>
          <div class="lp-clock-date">{{ currentDate }}</div>
        </div>

        <h2 class="lp-form-title">欢迎登录</h2>
        <p class="lp-form-sub">{{ greeting }}，请填写您的账号信息</p>

        <!-- 登录类型 -->
        <div class="lp-seg">
          <button class="lp-seg-btn" :class="{ active: loginForm.loginType === '1' }" @click="switchType('1')">域账号</button>
          <button class="lp-seg-btn" :class="{ active: loginForm.loginType === '0' }" @click="switchType('0')">系统账号</button>
        </div>

        <el-form ref="loginForm" :model="loginForm" :rules="currentRules" class="lp-form">
          <el-form-item prop="username">
            <div class="lp-field">
              <label>账号</label>
              <el-input v-model="loginForm.username" placeholder="请输入账号"
                auto-complete="off" size="medium" />
            </div>
          </el-form-item>

          <el-form-item prop="password">
            <div class="lp-field">
              <label>密码</label>
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"
                auto-complete="off" size="medium" show-password
                @keyup.enter.native="handleLogin" />
            </div>
          </el-form-item>

          <!-- 验证码与记住密码：始终占位，域账号时隐藏 -->
          <el-form-item prop="code" :class="{ 'lp-hidden': loginForm.loginType !== '0' }">
            <div class="lp-field">
              <label>验证码</label>
              <div class="lp-code-row">
                <el-input v-model="loginForm.code" placeholder="请输入验证码"
                  auto-complete="off" size="medium"
                  @keyup.enter.native="handleLogin" />
                <div class="lp-captcha" @click="getCode" title="点击刷新">
                  <img :src="codeUrl" alt="验证码"/>
                  <div class="lp-captcha-mask"><i class="el-icon-refresh"></i></div>
                </div>
              </div>
            </div>
          </el-form-item>
          <div class="lp-remember" :class="{ 'lp-hidden': loginForm.loginType !== '0' }">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
          </div>

          <el-button :loading="loading" type="primary" class="lp-btn"
            @click.native.prevent="handleLogin">
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form>

        <div class="lp-footer">
          <i class="el-icon-lock"></i> 数据加密传输，安全访问保障
        </div>
      </div>

      <div class="lp-copy">© 2024 会议管理系统 · All Rights Reserved</div>
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
        username: "", password: "", rememberMe: false,
        code: "", uuid: "", loginType: "1"
      },
      sysRules: {
        username: [{ required: true, trigger: "blur",   message: "请输入账号" }],
        password: [{ required: true, trigger: "blur",   message: "请输入密码" }],
        code:     [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      ldapRules: {
        username: [{ required: true, trigger: "blur", message: "请输入域账号" }],
        password: [{ required: true, trigger: "blur", message: "请输入密码" }]
      },
      loading: false,
      redirect: undefined,
      currentTime: "", currentDate: "", clockTimer: null
    };
  },
  computed: {
    currentRules() { return this.loginForm.loginType === "1" ? this.ldapRules : this.sysRules; },
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
    $route: { handler(r) { this.redirect = r.query && r.query.redirect; }, immediate: true }
  },
  created() {
    this.getCode(); this.getCookie();
    this.tickClock(); this.clockTimer = setInterval(this.tickClock, 1000);
  },
  beforeDestroy() { clearInterval(this.clockTimer); },
  methods: {
    tickClock() {
      const now = new Date(), p = n => String(n).padStart(2, '0');
      const days = ['日','一','二','三','四','五','六'];
      this.currentTime = `${p(now.getHours())}:${p(now.getMinutes())}:${p(now.getSeconds())}`;
      this.currentDate = `${now.getFullYear()}年${now.getMonth()+1}月${now.getDate()}日  周${days[now.getDay()]}`;
    },
    switchType(t) { this.loginForm.loginType = t; this.$nextTick(() => this.$refs.loginForm.clearValidate()); },
    getCode() {
      getCodeImg().then(res => { this.codeUrl = "data:image/gif;base64," + res.img; this.loginForm.uuid = res.uuid; });
    },
    getCookie() {
      const u = Cookies.get("username"), pw = Cookies.get("password"), rm = Cookies.get("rememberMe");
      if (u) { this.loginForm.username = u; this.loginForm.password = pw ? decrypt(pw) : ""; this.loginForm.rememberMe = rm === "true"; }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return;
        this.loading = true;
        if (this.loginForm.loginType === "0") {
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set("rememberMe", true, { expires: 30 });
          } else {
            Cookies.remove("username"); Cookies.remove("password"); Cookies.remove("rememberMe");
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
@keyframes lp-in {
  from { opacity: 0; transform: translateX(16px); }
  to   { opacity: 1; transform: translateX(0); }
}
@keyframes lp-illus-float {
  0%, 100% { transform: translateY(0); }
  50%       { transform: translateY(-8px); }
}

/* ══ 根 ══ */
.lp-root {
  min-height: 100vh;
  display: flex;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: #0f172a;
}

/* ══ 左侧 ══ */
.lp-brand {
  width: 55%;
  flex-shrink: 0;
  background: linear-gradient(135deg, #1e3a5f 0%, #162d4a 50%, #0f2035 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  @media (max-width: 768px) { display: none; }

  /* 背景大圆装饰 */
  &::before {
    content: '';
    position: absolute;
    width: 600px; height: 600px;
    border-radius: 50%;
    border: 1px solid rgba(255,255,255,0.04);
    top: -200px; right: -180px;
  }
  &::after {
    content: '';
    position: absolute;
    width: 400px; height: 400px;
    border-radius: 50%;
    border: 1px solid rgba(255,255,255,0.04);
    bottom: -150px; left: -100px;
  }
}

.lp-brand-content {
  position: relative;
  z-index: 1;
  padding: 48px 52px;
  width: 100%;
  display: flex;
  flex-direction: column;
}

/* Logo */
.lp-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 44px;
  svg { width: 28px; height: 28px; opacity: 0.9; }
  span {
    font-size: 15px;
    font-weight: 500;
    color: rgba(255,255,255,0.7);
    letter-spacing: 0.5px;
  }
}

/* 主标题 */
.lp-headline {
  margin-bottom: 36px;
  h1 {
    font-size: 36px;
    font-weight: 800;
    color: #ffffff;
    line-height: 1.2;
    letter-spacing: -0.5px;
    margin: 0 0 14px;
  }
  p {
    font-size: 13.5px;
    color: rgba(255,255,255,0.4);
    line-height: 1.8;
    margin: 0;
    max-width: 340px;
  }
}

/* 插图 */
.lp-illus {
  margin-bottom: 36px;
  svg {
    width: 100%;
    max-width: 440px;
    animation: lp-illus-float 6s ease-in-out infinite;
    filter: drop-shadow(0 16px 40px rgba(0,0,0,0.4));
  }
}

/* 底部统计 */
.lp-stats {
  display: flex;
  align-items: center;
  gap: 0;
  padding: 20px 24px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 12px;
  backdrop-filter: blur(8px);
}
.lp-stat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.lp-stat-n {
  font-size: 22px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: -0.5px;
}
.lp-stat-l {
  font-size: 11px;
  color: rgba(255,255,255,0.35);
  letter-spacing: 0.5px;
}
.lp-stat-div {
  width: 1px;
  height: 32px;
  background: rgba(255,255,255,0.1);
}

/* ══ 右侧 ══ */
.lp-panel {
  flex: 1;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  min-width: 0;
  animation: lp-in 0.5s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.lp-panel-inner {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 52px 52px 36px;
  max-width: 440px;
  width: 100%;
  margin: 0 auto;
}

/* 时钟 */
.lp-clock {
  margin-bottom: 36px;
  padding-bottom: 28px;
  border-bottom: 1px solid #f1f5f9;
}
.lp-clock-time {
  font-size: 38px;
  font-weight: 200;
  color: #0f172a;
  letter-spacing: 5px;
  font-variant-numeric: tabular-nums;
  line-height: 1;
  margin-bottom: 8px;
}
.lp-clock-date {
  font-size: 12.5px;
  color: #94a3b8;
  letter-spacing: 0.3px;
}

/* 标题 */
.lp-form-title {
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 6px;
  letter-spacing: -0.3px;
}
.lp-form-sub {
  font-size: 13.5px;
  color: #94a3b8;
  margin: 0 0 28px;
}

/* 分段控件 */
.lp-seg {
  display: flex;
  margin-bottom: 28px;
  background: #f8fafc;
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  padding: 3px;
  gap: 3px;
}
.lp-seg-btn {
  flex: 1;
  height: 34px;
  border: none;
  border-radius: 7px;
  font-size: 13.5px;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  color: #94a3b8;
  background: transparent;
  transition: all 0.18s;
  &.active {
    background: #ffffff;
    color: #0f172a;
    box-shadow: 0 1px 4px rgba(0,0,0,0.08), 0 0 0 1px rgba(0,0,0,0.04);
  }
  &:hover:not(.active) { color: #475569; }
}

/* 表单 */
.lp-form {
  .el-form-item { margin-bottom: 0; }
  .el-form-item__error { font-size: 11.5px; padding-top: 3px; color: #f43f5e; }
  .el-input__inner {
    height: 44px !important;
    border-radius: 10px !important;
    border: 1.5px solid #c8d0dc !important;
    background: #ffffff !important;
    color: #0f172a !important;
    font-size: 14px !important;
    padding: 0 14px !important;
    box-shadow: 0 1px 2px rgba(0,0,0,0.04) !important;
    transition: all 0.18s !important;
    &:focus {
      border-color: #2563eb !important;
      background: #ffffff !important;
      box-shadow: 0 0 0 3px rgba(37,99,235,0.12) !important;
    }
    &::placeholder { color: #b0bac6 !important; font-size: 13.5px !important; }
  }
  .el-input__suffix { line-height: 44px; }
  .el-input__prefix { display: none; }
}
.lp-field {
  display: flex;
  flex-direction: column;
  gap: 7px;
  padding-bottom: 18px;
  label {
    font-size: 13px;
    font-weight: 600;
    color: #374151;
    letter-spacing: 0.2px;
  }
}

/* 验证码 */
.lp-code-row { display: flex; gap: 10px; align-items: center; }
.lp-hidden {
  visibility: hidden;
  pointer-events: none;
}

.lp-captcha {
  position: relative;
  width: 110px;
  height: 44px;
  flex-shrink: 0;
  border-radius: 10px;
  overflow: hidden;
  border: 1.5px solid #c8d0dc;
  cursor: pointer;
  transition: border-color 0.18s;
  &:hover { border-color: #2563eb; .lp-captcha-mask { opacity: 1; } }
  img { width: 100%; height: 100%; object-fit: cover; display: block; }
}
.lp-captcha-mask {
  position: absolute; inset: 0;
  display: flex; align-items: center; justify-content: center;
  background: rgba(37,99,235,0.55);
  color: #fff; font-size: 18px;
  opacity: 0; transition: opacity 0.18s;
}

.lp-remember {
  margin: -8px 0 14px;
  .el-checkbox__label { font-size: 13px; color: #94a3b8; }
  .el-checkbox__inner { border-radius: 4px; border-color: #cbd5e1; }
  .el-checkbox__input.is-checked .el-checkbox__inner { background: #2563eb; border-color: #2563eb; }
}

/* 按钮 */
.lp-btn {
  width: 100%;
  height: 46px;
  margin-top: 6px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 3px;
  border-radius: 10px !important;
  background: #2563eb !important;
  border: none !important;
  box-shadow: 0 2px 8px rgba(37,99,235,0.3), 0 1px 2px rgba(0,0,0,0.06) !important;
  transition: all 0.2s !important;
  &:hover {
    background: #1d4ed8 !important;
    box-shadow: 0 6px 20px rgba(37,99,235,0.4) !important;
    transform: translateY(-1px);
  }
  &:active { transform: translateY(0); }
}

.lp-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 12px;
  color: #c1cbd8;
  i { margin-right: 5px; }
}

.lp-copy {
  padding: 18px 52px;
  font-size: 11.5px;
  color: #c1cbd8;
  border-top: 1px solid #f1f5f9;
  text-align: center;
}
</style>
