<template>
  <div class="lp-root">

    <!-- ══ 左侧内容区 ══ -->
    <div class="lp-left">
      <!-- 顶部 Logo -->
      <div class="lp-logo">
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
        <span class="lp-logo-text">会议管理系统</span>
      </div>

      <!-- 大标题 -->
      <h1 class="lp-hero-title">会议管理<br/>中心</h1>

      <!-- 装饰点阵 -->
      <div class="lp-dots">
        <span v-for="i in 25" :key="i" class="lp-dot"></span>
      </div>

      <!-- 公告列表 -->
      <div class="lp-section">
        <div class="lp-section-title">
          <i class="el-icon-bell"></i> 近期会议安排
        </div>
        <ul class="lp-list">
          <li><span class="lp-list-tag tag-blue">研发部</span><span class="lp-list-text">2026年Q2产品规划会议</span><span class="lp-list-date">今天</span></li>
          <li><span class="lp-list-tag tag-purple">管理层</span><span class="lp-list-text">月度经营分析会</span><span class="lp-list-date">明天</span></li>
          <li><span class="lp-list-tag tag-green">全员</span><span class="lp-list-text">2026年上半年总结大会</span><span class="lp-list-date">05-06</span></li>
          <li><span class="lp-list-tag tag-blue">技术部</span><span class="lp-list-text">系统架构评审会</span><span class="lp-list-date">05-08</span></li>
          <li><span class="lp-list-tag tag-orange">运营部</span><span class="lp-list-text">用户增长策略讨论</span><span class="lp-list-date">05-10</span></li>
        </ul>
      </div>

      <div class="lp-section">
        <div class="lp-section-title">
          <i class="el-icon-office-building"></i> 会议室状态
        </div>
        <ul class="lp-list">
          <li><span class="lp-list-tag tag-green">空闲</span><span class="lp-list-text">第一会议室（10人）</span><span class="lp-list-date">可预约</span></li>
          <li><span class="lp-list-tag tag-red">使用中</span><span class="lp-list-text">第二会议室（20人）</span><span class="lp-list-date">至15:00</span></li>
          <li><span class="lp-list-tag tag-green">空闲</span><span class="lp-list-text">小型洽谈室（4人）</span><span class="lp-list-date">可预约</span></li>
        </ul>
      </div>
    </div>

    <!-- ══ 右侧蓝色背景区 ══ -->
    <div class="lp-right">
      <!-- 浮动图标气泡 -->
      <div class="lp-bubble lp-bubble1">
        <i class="el-icon-date"></i>
      </div>
      <div class="lp-bubble lp-bubble2">
        <i class="el-icon-user"></i>
      </div>
      <div class="lp-bubble lp-bubble3">
        <i class="el-icon-bell"></i>
      </div>
      <div class="lp-bubble lp-bubble4">
        <i class="el-icon-video-camera"></i>
      </div>
      <div class="lp-bubble lp-bubble5">
        <i class="el-icon-office-building"></i>
      </div>
    </div>

    <!-- ══ 浮动登录卡片 ══ -->
    <div class="lp-card">
      <h2 class="lp-card-title">账号登录</h2>

      <!-- 类型切换 -->
      <div class="lp-type-switch">
        <button class="lp-type-btn" :class="{ active: loginForm.loginType === '1' }" @click="switchType('1')">域账号</button>
        <button class="lp-type-btn" :class="{ active: loginForm.loginType === '0' }" @click="switchType('0')">系统账号</button>
      </div>

      <el-form ref="loginForm" :model="loginForm" :rules="currentRules" class="lp-form">
        <el-form-item prop="username">
          <div class="lp-field-wrap">
            <label class="lp-label">* 账号</label>
            <el-input v-model="loginForm.username" placeholder="请输入账号" auto-complete="off"
              prefix-icon="el-icon-user" size="medium" />
          </div>
        </el-form-item>

        <el-form-item prop="password">
          <div class="lp-field-wrap">
            <label class="lp-label">* 密码</label>
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"
              auto-complete="off" prefix-icon="el-icon-lock" size="medium" show-password
              @keyup.enter.native="handleLogin" />
          </div>
        </el-form-item>

        <template v-if="loginForm.loginType === '0'">
          <el-form-item prop="code">
            <div class="lp-field-wrap">
              <label class="lp-label">* 验证码</label>
              <div class="lp-code-row">
                <el-input v-model="loginForm.code" placeholder="请输入验证码"
                  auto-complete="off" prefix-icon="el-icon-key" size="medium"
                  @keyup.enter.native="handleLogin" />
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
          <span v-if="!loading">登 录</span>
          <span v-else>登录中...</span>
        </el-button>
      </el-form>

      <p class="lp-helptext">如忘记密码，请联系系统管理员</p>

      <div class="lp-clock-bar">
        <span>{{ currentDate }}</span>
        <span class="lp-clock-time">{{ currentTime }}</span>
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
      this.currentTime = `${p(now.getHours())}:${p(now.getMinutes())}`;
      this.currentDate = `${now.getFullYear()}-${p(now.getMonth()+1)}-${p(now.getDate())} 周${days[now.getDay()]}`;
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
@keyframes lp-float-a {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50%       { transform: translateY(-14px) rotate(3deg); }
}
@keyframes lp-float-b {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50%       { transform: translateY(-18px) rotate(-4deg); }
}
@keyframes lp-float-c {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50%       { transform: translateY(-10px) rotate(5deg); }
}
@keyframes lp-card-in {
  from { opacity: 0; transform: translate(-50%, -46%) scale(0.97); }
  to   { opacity: 1; transform: translate(-50%, -50%) scale(1); }
}

/* ══ 根容器 ══ */
.lp-root {
  position: relative;
  min-height: 100vh;
  display: flex;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: #f5f7fb;
}

/* ══ 左侧内容区 ══ */
.lp-left {
  width: 52%;
  min-height: 100vh;
  padding: 40px 48px;
  background: #ffffff;
  position: relative;
  z-index: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  @media (max-width: 768px) { width: 100%; }
}

/* Logo */
.lp-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 36px;
}
.lp-logo-icon {
  width: 36px; height: 36px;
  background: #2563eb;
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  color: #fff;
  svg { width: 18px; height: 18px; }
}
.lp-logo-text {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  letter-spacing: 0.5px;
}

/* 大标题 */
.lp-hero-title {
  font-size: 52px;
  font-weight: 900;
  color: #0f172a;
  line-height: 1.1;
  letter-spacing: -1px;
  margin: 0 0 32px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 装饰点阵 */
.lp-dots {
  position: absolute;
  top: 60px;
  right: 40px;
  display: grid;
  grid-template-columns: repeat(5, 8px);
  gap: 6px;
  opacity: 0.35;
}
.lp-dot {
  width: 4px; height: 4px;
  border-radius: 50%;
  background: #94a3b8;
}

/* 信息区块 */
.lp-section {
  margin-bottom: 28px;
}
.lp-section-title {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 12px;
  letter-spacing: 0.3px;
  i { margin-right: 6px; color: #2563eb; }
}
.lp-list {
  list-style: none;
  padding: 0; margin: 0;
  li {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 9px 0;
    border-bottom: 1px solid #f1f5f9;
    &:last-child { border-bottom: none; }
  }
}
.lp-list-tag {
  flex-shrink: 0;
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 4px;
  min-width: 48px;
  text-align: center;
  &.tag-blue   { background: #eff6ff; color: #2563eb; }
  &.tag-purple { background: #f5f3ff; color: #7c3aed; }
  &.tag-green  { background: #f0fdf4; color: #16a34a; }
  &.tag-orange { background: #fff7ed; color: #ea580c; }
  &.tag-red    { background: #fef2f2; color: #dc2626; }
}
.lp-list-text {
  flex: 1;
  font-size: 13px;
  color: #334155;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.lp-list-date {
  flex-shrink: 0;
  font-size: 11.5px;
  color: #94a3b8;
}

/* ══ 右侧蓝色背景 ══ */
.lp-right {
  flex: 1;
  min-height: 100vh;
  background: linear-gradient(150deg, #2563eb 0%, #1d4ed8 45%, #4f46e5 100%);
  position: relative;
  overflow: hidden;
}

/* 气泡图标 */
.lp-bubble {
  position: absolute;
  border-radius: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 8px 32px rgba(0,0,0,0.2);
  i { font-size: 26px; }
}
.lp-bubble1 {
  width: 70px; height: 70px;
  background: #7c3aed;
  top: 18%; left: 20%;
  animation: lp-float-a 5s ease-in-out infinite;
}
.lp-bubble2 {
  width: 60px; height: 60px;
  background: #0ea5e9;
  top: 12%; right: 18%;
  animation: lp-float-b 6.5s ease-in-out infinite;
  animation-delay: -2s;
}
.lp-bubble3 {
  width: 56px; height: 56px;
  background: #10b981;
  bottom: 28%; left: 14%;
  animation: lp-float-c 7s ease-in-out infinite;
  animation-delay: -1s;
}
.lp-bubble4 {
  width: 64px; height: 64px;
  background: #f59e0b;
  bottom: 18%; right: 16%;
  animation: lp-float-a 5.5s ease-in-out infinite;
  animation-delay: -3s;
}
.lp-bubble5 {
  width: 50px; height: 50px;
  background: rgba(255,255,255,0.15);
  border: 1.5px solid rgba(255,255,255,0.3);
  top: 55%; right: 28%;
  animation: lp-float-b 8s ease-in-out infinite;
  animation-delay: -4s;
}

/* ══ 浮动登录卡片 ══ */
.lp-card {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 10;
  width: 360px;
  background: #ffffff;
  border-radius: 16px;
  padding: 36px 32px 28px;
  box-shadow:
    0 4px 6px rgba(0,0,0,0.05),
    0 20px 60px rgba(0,0,0,0.2),
    0 0 0 1px rgba(255,255,255,0.1);
  animation: lp-card-in 0.5s cubic-bezier(0.22,1,0.36,1) both;

  @media (max-width: 768px) {
    position: fixed;
    width: calc(100% - 32px);
    max-width: 360px;
    left: 50%; top: 50%;
  }
}

.lp-card-title {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 20px;
  letter-spacing: 0.3px;
}

/* 类型切换 */
.lp-type-switch {
  display: flex;
  gap: 0;
  margin-bottom: 20px;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}
.lp-type-btn {
  flex: 1;
  height: 34px;
  border: none;
  background: transparent;
  font-size: 13px;
  color: #94a3b8;
  cursor: pointer;
  font-family: inherit;
  font-weight: 500;
  transition: all 0.18s;
  &:first-child { border-right: 1.5px solid #e2e8f0; }
  &.active {
    background: #2563eb;
    color: #ffffff;
    border-color: #2563eb;
  }
  &:hover:not(.active) { background: #f8fafc; color: #475569; }
}

/* 表单 */
.lp-form {
  .el-form-item { margin-bottom: 0; }
  .el-form-item__error { font-size: 11px; padding-top: 2px; }
  .el-input__inner {
    border-top: none !important;
    border-left: none !important;
    border-right: none !important;
    border-bottom: 1.5px solid #e2e8f0 !important;
    border-radius: 0 !important;
    background: transparent !important;
    box-shadow: none !important;
    height: 42px !important;
    font-size: 14px !important;
    color: #0f172a !important;
    padding-left: 30px !important;
    transition: border-color 0.15s !important;
    &:focus { border-bottom-color: #2563eb !important; box-shadow: none !important; }
    &::placeholder { color: #cbd5e1 !important; }
  }
  .el-input__prefix { color: #94a3b8; line-height: 42px; }
  .el-input__suffix { line-height: 42px; }
}
.lp-field-wrap {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-bottom: 14px;
}
.lp-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

/* 验证码 */
.lp-code-row { display: flex; gap: 8px; align-items: flex-end; }
.lp-captcha {
  position: relative;
  width: 96px; height: 42px;
  flex-shrink: 0;
  border-bottom: 1.5px solid #e2e8f0;
  cursor: pointer;
  overflow: hidden;
  &:hover .lp-captcha-mask { opacity: 1; }
  img { width: 100%; height: 100%; object-fit: cover; display: block; }
}
.lp-captcha-mask {
  position: absolute; inset: 0;
  display: flex; align-items: center; justify-content: center;
  background: rgba(37,99,235,0.6);
  color: #fff; font-size: 18px;
  opacity: 0; transition: opacity 0.18s;
}

.lp-remember {
  margin: -6px 0 12px;
  .el-checkbox__label { font-size: 12px; color: #94a3b8; }
  .el-checkbox__inner { border-radius: 3px; border-color: #cbd5e1; }
  .el-checkbox__input.is-checked .el-checkbox__inner { background-color: #2563eb; border-color: #2563eb; }
}

/* 提交按钮 */
.lp-submit {
  width: 100%;
  height: 42px;
  margin-top: 8px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 3px;
  border-radius: 8px !important;
  background: #2563eb !important;
  border-color: #2563eb !important;
  box-shadow: 0 4px 14px rgba(37,99,235,0.35) !important;
  transition: all 0.2s !important;
  &:hover {
    background: #1d4ed8 !important;
    border-color: #1d4ed8 !important;
    box-shadow: 0 6px 20px rgba(37,99,235,0.45) !important;
  }
}

.lp-helptext {
  text-align: center;
  font-size: 11.5px;
  color: #94a3b8;
  margin: 16px 0 0;
}

/* 底部时间栏 */
.lp-clock-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid #f1f5f9;
  font-size: 11.5px;
  color: #cbd5e1;
}
.lp-clock-time {
  font-variant-numeric: tabular-nums;
  font-weight: 500;
  color: #94a3b8;
}
</style>
