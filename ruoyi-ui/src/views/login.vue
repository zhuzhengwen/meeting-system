<template>
  <div class="lp-root">

    <div class="lp-wrap">
      <!-- 左侧品牌区 -->
      <div class="lp-brand">
        <div class="lp-particles">
          <span v-for="i in 20" :key="i" class="lp-dot" :style="dotStyle(i)"></span>
        </div>

        <div class="lp-brand-body">
          <h1 class="lp-brand-title">会议管理系统</h1>
          <p class="lp-brand-en">MEETING MANAGEMENT SYSTEM</p>
          <div class="lp-illus-wrap">
            <svg class="lp-illus" viewBox="0 0 280 260" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <filter id="glow">
                  <feGaussianBlur stdDeviation="2.5" result="blur"/>
                  <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
                </filter>
                <filter id="softShadow">
                  <feDropShadow dx="0" dy="6" stdDeviation="12" flood-color="rgba(0,0,0,0.25)"/>
                </filter>
              </defs>

              <!-- 背景装饰圆 -->
              <circle cx="140" cy="130" r="105" fill="rgba(147,197,253,0.04)"/>

              <!-- ── 主会议卡片 ── -->
              <rect x="42" y="44" width="178" height="158" rx="16" fill="rgba(255,255,255,0.13)" stroke="rgba(255,255,255,0.32)" stroke-width="1.5" filter="url(#softShadow)"/>
              <!-- 卡片顶部色条 -->
              <rect x="42" y="44" width="178" height="5" rx="2.5" fill="rgba(147,197,253,0.8)"/>

              <!-- 日历图标 -->
              <rect x="60" y="62" width="22" height="22" rx="5" fill="none" stroke="rgba(255,255,255,0.65)" stroke-width="1.5"/>
              <line x1="67" y1="58" x2="67" y2="66" stroke="rgba(255,255,255,0.65)" stroke-width="1.5" stroke-linecap="round"/>
              <line x1="75" y1="58" x2="75" y2="66" stroke="rgba(255,255,255,0.65)" stroke-width="1.5" stroke-linecap="round"/>
              <line x1="60" y1="71" x2="82" y2="71" stroke="rgba(255,255,255,0.35)" stroke-width="1"/>
              <!-- 日历格子 -->
              <rect x="63" y="74" width="4" height="4" rx="1" fill="rgba(255,255,255,0.55)"/>
              <rect x="70" y="74" width="4" height="4" rx="1" fill="rgba(147,197,253,0.9)"/>
              <rect x="77" y="74" width="4" height="4" rx="1" fill="rgba(255,255,255,0.35)"/>

              <!-- 标题文字 -->
              <rect x="92" y="65" width="90" height="7" rx="3.5" fill="rgba(255,255,255,0.8)"/>
              <rect x="92" y="76" width="62" height="4" rx="2" fill="rgba(255,255,255,0.38)"/>

              <!-- 分隔线 -->
              <line x1="58" y1="98" x2="202" y2="98" stroke="rgba(255,255,255,0.08)" stroke-width="1"/>

              <!-- 时间进度条组 -->
              <rect x="58" y="106" width="144" height="9" rx="4.5" fill="rgba(255,255,255,0.07)"/>
              <rect x="58" y="106" width="96"  height="9" rx="4.5" fill="rgba(96,165,250,0.55)"/>

              <rect x="58" y="121" width="144" height="9" rx="4.5" fill="rgba(255,255,255,0.07)"/>
              <rect x="72" y="121" width="68"  height="9" rx="4.5" fill="rgba(167,139,250,0.55)"/>

              <rect x="58" y="136" width="144" height="9" rx="4.5" fill="rgba(255,255,255,0.07)"/>
              <rect x="90" y="136" width="50"  height="9" rx="4.5" fill="rgba(110,231,183,0.55)"/>

              <!-- 分隔线 -->
              <line x1="58" y1="156" x2="202" y2="156" stroke="rgba(255,255,255,0.08)" stroke-width="1"/>

              <!-- 参会人头像 -->
              <circle cx="72"  cy="173" r="11" fill="rgba(96,165,250,0.75)"  stroke="rgba(255,255,255,0.6)" stroke-width="1.8"/>
              <circle cx="91"  cy="173" r="11" fill="rgba(167,139,250,0.75)" stroke="rgba(255,255,255,0.6)" stroke-width="1.8"/>
              <circle cx="110" cy="173" r="11" fill="rgba(110,231,183,0.75)" stroke="rgba(255,255,255,0.6)" stroke-width="1.8"/>
              <circle cx="129" cy="173" r="11" fill="rgba(251,191,36,0.75)"  stroke="rgba(255,255,255,0.6)" stroke-width="1.8"/>
              <circle cx="148" cy="173" r="11" fill="rgba(255,255,255,0.12)" stroke="rgba(255,255,255,0.38)" stroke-width="1.8"/>
              <text x="148" y="177" text-anchor="middle" fill="rgba(255,255,255,0.75)" font-size="9" font-family="Arial,sans-serif">+3</text>

              <!-- ── 浮动卡片：已确认徽章 ── -->
              <rect x="172" y="22" width="88" height="30" rx="15" fill="rgba(52,211,153,0.88)" filter="url(#softShadow)"/>
              <path d="M188 37 L191 40 L200 32" stroke="white" stroke-width="1.8" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
              <rect x="206" y="33" width="42" height="5" rx="2.5" fill="rgba(255,255,255,0.88)"/>
              <rect x="206" y="41" width="30" height="3.5" rx="1.75" fill="rgba(255,255,255,0.5)"/>

              <!-- ── 浮动卡片：时钟提醒 ── -->
              <rect x="6" y="90" width="72" height="58" rx="12" fill="rgba(255,255,255,0.11)" stroke="rgba(255,255,255,0.22)" stroke-width="1"/>
              <circle cx="28" cy="112" r="11" fill="none" stroke="rgba(147,197,253,0.75)" stroke-width="1.5"/>
              <line x1="28" y1="104" x2="28" y2="112" stroke="rgba(147,197,253,0.9)" stroke-width="1.5" stroke-linecap="round"/>
              <line x1="28" y1="112" x2="33" y2="116" stroke="rgba(147,197,253,0.9)" stroke-width="1.5" stroke-linecap="round"/>
              <rect x="45" y="107" width="26" height="5" rx="2.5" fill="rgba(255,255,255,0.6)"/>
              <rect x="45" y="115" width="18" height="3.5" rx="1.75" fill="rgba(255,255,255,0.3)"/>
              <line x1="14" y1="128" x2="70" y2="128" stroke="rgba(255,255,255,0.08)" stroke-width="1"/>
              <rect x="14" y="134" width="54" height="3.5" rx="1.75" fill="rgba(255,255,255,0.18)"/>

              <!-- ── 浮动卡片：通知 ── -->
              <rect x="208" y="172" width="70" height="60" rx="12" fill="rgba(255,255,255,0.11)" stroke="rgba(255,255,255,0.22)" stroke-width="1"/>
              <circle cx="224" cy="191" r="8" fill="rgba(96,165,250,0.35)" stroke="rgba(96,165,250,0.7)" stroke-width="1.5"/>
              <rect x="238" y="187" width="32" height="5" rx="2.5" fill="rgba(255,255,255,0.6)"/>
              <rect x="238" y="195" width="22" height="3.5" rx="1.75" fill="rgba(255,255,255,0.3)"/>
              <line x1="216" y1="207" x2="270" y2="207" stroke="rgba(255,255,255,0.08)" stroke-width="1"/>
              <rect x="216" y="214" width="22" height="3.5" rx="1.75" fill="rgba(255,255,255,0.22)"/>
              <rect x="244" y="214" width="18" height="3.5" rx="1.75" fill="rgba(255,255,255,0.22)"/>

              <!-- 装饰光点 -->
              <circle cx="22"  cy="50"  r="3"   fill="rgba(147,197,253,0.55)" filter="url(#glow)"/>
              <circle cx="42"  cy="30"  r="2"   fill="rgba(147,197,253,0.4)"/>
              <circle cx="250" cy="60"  r="3.5" fill="rgba(167,139,250,0.5)"  filter="url(#glow)"/>
              <circle cx="14"  cy="170" r="2.5" fill="rgba(110,231,183,0.45)"/>
              <circle cx="262" cy="148" r="2"   fill="rgba(147,197,253,0.4)"/>
              <circle cx="248" cy="244" r="3"   fill="rgba(251,191,36,0.4)"   filter="url(#glow)"/>
            </svg>
            <div class="lp-spin-ring lp-spin-cw"></div>
            <div class="lp-spin-ring lp-spin-ccw"></div>
          </div>

          <!-- 底部特性标签 -->
          <div class="lp-tags">
            <span class="lp-tag"><i class="el-icon-office-building"></i> 会议室预约</span>
            <span class="lp-tag"><i class="el-icon-user"></i> 多人协同</span>
            <span class="lp-tag"><i class="el-icon-bell"></i> 消息通知</span>
          </div>

          <p class="lp-slogan">高效管理 · 智能协作 · 无缝沟通</p>
        </div>

        <div class="lp-bgc lp-bgc1"></div>
        <div class="lp-bgc lp-bgc2"></div>
      </div>

      <!-- 右侧表单区 -->
      <div class="lp-panel">
        <!-- 顶部彩条 -->
        <div class="lp-card-bar"></div>

        <div class="lp-panel-body">
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

            <!-- 验证码行：始终占位，仅在域账号时隐藏 -->
            <el-form-item prop="code" class="lp-code-row"
              :class="{ 'lp-field-hidden': loginForm.loginType !== '0' }">
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

            <!-- 记住密码：始终占位，仅在域账号时隐藏 -->
            <div class="lp-remember" :class="{ 'lp-field-hidden': loginForm.loginType !== '0' }">
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
    </div>

    <p class="lp-copy">Copyright © 2024 会议管理系统 &nbsp;|&nbsp; All Rights Reserved</p>
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
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.3; }
  50%       { transform: translateY(-22px) scale(1.4); opacity: 0.65; }
}
@keyframes lp-spin-cw  { to { transform: rotate(360deg);  } }
@keyframes lp-spin-ccw { to { transform: rotate(-360deg); } }
@keyframes lp-pulse {
  0%, 100% { opacity: 0.75; transform: scale(1); }
  50%       { opacity: 1;    transform: scale(1.05); }
}
@keyframes lp-slide-up {
  from { opacity: 0; transform: translateY(18px); }
  to   { opacity: 1; transform: translateY(0); }
}
@keyframes lp-bar-shine {
  0%   { background-position: -200% center; }
  100% { background-position:  200% center; }
}

/* ════════════ 根容器 ════════════ */
.lp-root {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #dde8f8 0%, #cddaf4 50%, #d8e6f8 100%);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', sans-serif;
  padding: 24px;
  transition: background 0.3s;

}

/* ── 居中卡片容器 ── */
.lp-wrap {
  display: flex;
  width: 100%;
  max-width: 980px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow:
    0 24px 64px rgba(26, 86, 219, 0.18),
    0 4px 16px rgba(0, 0, 0, 0.10);
  animation: lp-slide-up 0.6s ease both;
}

/* ════════════ 左侧品牌区 ════════════ */
.lp-brand {
  position: relative;
  width: 52%;
  flex-shrink: 0;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    linear-gradient(rgba(255,255,255,0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.05) 1px, transparent 1px),
    linear-gradient(150deg, #1535a8 0%, #1e52d8 40%, #2a74f0 75%, #3b8ef5 100%);
  background-size: 50px 50px, 50px 50px, cover;
  @media (max-width: 680px) { display: none; }
}

.lp-particles { position: absolute; inset: 0; pointer-events: none; }
.lp-dot {
  position: absolute;
  border-radius: 50%;
  background: rgba(147, 197, 253, 0.75);
  animation: lp-float ease-in-out infinite;
}

.lp-brand-body {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 36px 48px;
}

/* 插图容器 */
.lp-illus-wrap {
  position: relative;
  width: 290px;
  height: 270px;
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
  width: 276px; height: 256px;
  border: 1px dashed rgba(147,197,253,0.35);
  border-top-color: rgba(147,210,253,0.75);
  border-radius: 50%;
  animation: lp-spin-cw 20s linear infinite;
}
.lp-spin-ccw {
  width: 244px; height: 226px;
  border: 1px dashed rgba(147,197,253,0.22);
  border-right-color: rgba(147,197,253,0.6);
  border-radius: 50%;
  animation: lp-spin-ccw 14s linear infinite;
}
.lp-illus {
  width: 230px; height: 213px;
  display: block;
  animation: lp-pulse 4s ease-in-out infinite;
}

.lp-rule {
  width: 100%;
  height: 1px;
  background: linear-gradient(90deg,
    transparent 0%,
    rgba(255,255,255,0.25) 25%,
    rgba(255,255,255,0.25) 75%,
    transparent 100%
  );
  margin: 12px 0;
}

.lp-brand-title {
  color: #fff;
  font-size: 26px;
  font-weight: 500;
  letter-spacing: 8px;
  margin: 0 0 8px;
  text-shadow: 0 2px 16px rgba(0,0,0,0.25);
}
.lp-brand-en {
  color: rgba(255,255,255,0.4);
  font-size: 10px;
  letter-spacing: 4px;
  margin: 6px 0 20px;
}

.lp-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 14px 0 4px;
}
.lp-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 16px;
}
.lp-stat-n {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  line-height: 1.1;
  sup { font-size: 10px; font-weight: 400; color: #93c5fd; vertical-align: super; }
}
.lp-stat-l {
  font-size: 10px;
  color: rgba(255,255,255,0.45);
  margin-top: 3px;
  letter-spacing: 1px;
}
.lp-stat-sep {
  width: 1px; height: 26px;
  background: rgba(255,255,255,0.15);
}

.lp-tags {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 18px;
}
.lp-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 13px;
  border-radius: 20px;
  border: 1px solid rgba(255,255,255,0.2);
  background: rgba(255,255,255,0.08);
  color: rgba(255,255,255,0.75);
  font-size: 11.5px;
  letter-spacing: 0.5px;
  backdrop-filter: blur(4px);
  transition: all 0.2s;
  i { font-size: 12px; color: #93c5fd; }
  &:hover {
    background: rgba(255,255,255,0.14);
    border-color: rgba(255,255,255,0.35);
    color: #fff;
  }
}

.lp-slogan {
  margin: 14px 0 0;
  font-size: 11px;
  color: rgba(255,255,255,0.3);
  letter-spacing: 2px;
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
    color: rgba(255,255,255,0.65);
    font-size: 12px;
    letter-spacing: 0.5px;
    margin-bottom: 9px;
    transition: color 0.2s;
    &:hover { color: rgba(255,255,255,0.95); }
    &::before {
      content: '—';
      position: absolute;
      left: 0;
      color: #93c5fd;
      font-size: 10px;
      line-height: inherit;
    }
    &:last-child { margin-bottom: 0; }
  }
}

.lp-bgc {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255,255,255,0.06);
  pointer-events: none;
}
.lp-bgc1 { width: 500px; height: 500px; top: -210px; right: -170px; }
.lp-bgc2 { width: 300px; height: 300px; bottom: -120px; left: -100px; }

/* ════════════ 右侧表单区 ════════════ */
.lp-panel {
  flex: 1;
  background: #fff;
  display: flex;
  flex-direction: column;
  position: relative;
  min-width: 0;
  transition: background 0.3s;
}

/* 顶部彩条 */
.lp-card-bar {
  height: 3px;
  flex-shrink: 0;
  background: linear-gradient(90deg,
    #1a56db 0%, #60a5fa 40%, #a5f3fc 55%, #60a5fa 70%, #1a56db 100%
  );
  background-size: 200% auto;
  animation: lp-bar-shine 3s linear infinite;
}


.lp-panel-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 36px 44px 40px;
  overflow-y: auto;
}

/* 实时时钟 */
.lp-clock-wrap {
  text-align: center;
  margin-bottom: 18px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f4ff;
  transition: border-color 0.3s;
}
.lp-clock-time {
  font-size: 30px;
  font-weight: 200;
  color: #1a56db;
  letter-spacing: 4px;
  font-variant-numeric: tabular-nums;
  line-height: 1;
  transition: color 0.3s;
}
.lp-clock-meta {
  margin-top: 5px;
  font-size: 11px;
  color: #b0bcd0;
  letter-spacing: 0.5px;
  transition: color 0.3s;
}
.lp-clock-sep { margin: 0 5px; }
.lp-clock-greet { color: #1a56db; transition: color 0.3s; }

.lp-card-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 5px;
  letter-spacing: 0.5px;
  transition: color 0.3s;
}
.lp-card-desc {
  font-size: 12px;
  color: #9ca3af;
  margin: 0 0 20px;
  letter-spacing: 0.3px;
  transition: color 0.3s;
}

/* tabs */
.lp-tabs {
  margin-bottom: 20px;
  .el-tabs__header { margin-bottom: 0; }
  .el-tabs__nav-wrap::after { height: 1px; background: #f0f4ff; transition: background 0.3s; }
  .el-tabs__item {
    font-size: 13px;
    color: #9ca3af;
    padding: 0 16px;
    height: 34px;
    line-height: 34px;
    transition: color 0.3s;
    &.is-active { color: #1a56db; font-weight: 600; }
  }
  .el-tabs__active-bar { background: #1a56db; height: 2px; }
}

/* 下划线风格输入框 */
.lp-form {
  .el-form-item { margin-bottom: 20px; }
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
    transition: border-color .2s, color 0.3s !important;
  }
  .el-input__inner:focus {
    border-bottom-color: #1a56db !important;
    background: transparent !important;
    box-shadow: none !important;
  }
  .el-input__prefix { line-height: 44px; color: #b0bcd0; transition: color 0.3s; }
  .el-input__suffix { line-height: 44px; }
  .el-form-item__error { padding-top: 4px; font-size: 11px; }
}

/* 隐藏但保留占位高度 */
.lp-field-hidden {
  visibility: hidden;
  pointer-events: none;
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
  .el-checkbox__label { font-size: 12px; color: #9ca3af; transition: color 0.3s; }
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

.lp-secure {
  margin-top: 18px;
  text-align: center;
  font-size: 11px;
  color: #c0c8d8;
  transition: color 0.3s;
  i { margin-right: 4px; }
}

.lp-copy {
  margin-top: 18px;
  font-size: 11px;
  color: #94a3b8;
  letter-spacing: 0.5px;
  text-align: center;
  transition: color 0.3s;
}
</style>
