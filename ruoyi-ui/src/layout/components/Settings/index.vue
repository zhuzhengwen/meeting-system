<template>
  <div class="mms-settings">

    <!-- 主题风格 -->
    <div class="mms-settings-section">
      <div class="mms-settings-heading">侧边栏风格</div>
      <div class="mms-theme-row">
        <div
          class="mms-theme-card"
          :class="{ active: sideTheme === 'theme-dark' }"
          @click="handleTheme('theme-dark')"
        >
          <div class="mms-theme-preview dark">
            <div class="tp-sidebar"></div>
            <div class="tp-content"></div>
          </div>
          <span class="mms-theme-label">深色</span>
          <i v-if="sideTheme === 'theme-dark'" class="el-icon-check mms-theme-check"></i>
        </div>
        <div
          class="mms-theme-card"
          :class="{ active: sideTheme === 'theme-light' }"
          @click="handleTheme('theme-light')"
        >
          <div class="mms-theme-preview light">
            <div class="tp-sidebar"></div>
            <div class="tp-content"></div>
          </div>
          <span class="mms-theme-label">浅色</span>
          <i v-if="sideTheme === 'theme-light'" class="el-icon-check mms-theme-check"></i>
        </div>
      </div>
    </div>

    <div class="mms-settings-divider"></div>

    <!-- 主题色 -->
    <div class="mms-settings-section">
      <div class="mms-settings-heading">主题颜色</div>
      <div class="mms-color-row">
        <span class="mms-color-label">点击选择主题色</span>
        <theme-picker style="margin-left:auto;" @change="themeChange" />
      </div>
    </div>

    <div class="mms-settings-divider"></div>

    <!-- 布局开关 -->
    <div class="mms-settings-section">
      <div class="mms-settings-heading">界面布局</div>

      <div class="mms-switch-row">
        <div class="mms-switch-info">
          <span class="mms-switch-name">顶部导航</span>
          <span class="mms-switch-desc">启用顶部横向导航菜单</span>
        </div>
        <el-switch v-model="topNav" active-color="#1a56db" />
      </div>

      <div class="mms-switch-row">
        <div class="mms-switch-info">
          <span class="mms-switch-name">标签页</span>
          <span class="mms-switch-desc">显示页面多标签切换栏</span>
        </div>
        <el-switch v-model="tagsView" active-color="#1a56db" />
      </div>

      <div class="mms-switch-row">
        <div class="mms-switch-info">
          <span class="mms-switch-name">固定顶栏</span>
          <span class="mms-switch-desc">滚动时顶部导航栏保持固定</span>
        </div>
        <el-switch v-model="fixedHeader" active-color="#1a56db" />
      </div>

      <div class="mms-switch-row">
        <div class="mms-switch-info">
          <span class="mms-switch-name">侧边 Logo</span>
          <span class="mms-switch-desc">显示侧边栏顶部系统 Logo</span>
        </div>
        <el-switch v-model="sidebarLogo" active-color="#1a56db" />
      </div>
    </div>

    <div class="mms-settings-divider"></div>

    <!-- 操作按钮 -->
    <div class="mms-settings-actions">
      <el-button type="primary" size="small" icon="el-icon-check" @click="saveSetting">保存配置</el-button>
      <el-button size="small" icon="el-icon-refresh" @click="resetSetting">重置</el-button>
    </div>

  </div>
</template>

<script>
import ThemePicker from '@/components/ThemePicker'

export default {
  components: { ThemePicker },
  data() {
    return {
      theme: this.$store.state.settings.theme,
      sideTheme: this.$store.state.settings.sideTheme
    };
  },
  computed: {
    fixedHeader: {
      get() { return this.$store.state.settings.fixedHeader },
      set(val) { this.$store.dispatch('settings/changeSetting', { key: 'fixedHeader', value: val }) }
    },
    topNav: {
      get() { return this.$store.state.settings.topNav },
      set(val) {
        this.$store.dispatch('settings/changeSetting', { key: 'topNav', value: val })
        if (!val) {
          this.$store.commit('SET_SIDEBAR_ROUTERS', this.$store.state.permission.defaultRoutes)
        }
      }
    },
    tagsView: {
      get() { return this.$store.state.settings.tagsView },
      set(val) { this.$store.dispatch('settings/changeSetting', { key: 'tagsView', value: val }) }
    },
    sidebarLogo: {
      get() { return this.$store.state.settings.sidebarLogo },
      set(val) { this.$store.dispatch('settings/changeSetting', { key: 'sidebarLogo', value: val }) }
    }
  },
  methods: {
    themeChange(val) {
      this.$store.dispatch('settings/changeSetting', { key: 'theme', value: val })
      this.theme = val
    },
    handleTheme(val) {
      this.$store.dispatch('settings/changeSetting', { key: 'sideTheme', value: val })
      this.sideTheme = val
    },
    saveSetting() {
      const loading = this.$loading({
        lock: true, fullscreen: false,
        text: '正在保存到本地，请稍后...',
        spinner: 'el-icon-loading',
        background: 'rgba(0,0,0,0.5)'
      })
      localStorage.setItem('layout-setting', JSON.stringify({
        topNav: this.topNav,
        tagsView: this.tagsView,
        fixedHeader: this.fixedHeader,
        sidebarLogo: this.sidebarLogo,
        sideTheme: this.sideTheme,
        theme: this.theme
      }))
      setTimeout(() => loading.close(), 800)
    },
    resetSetting() {
      this.$loading({
        lock: true, fullscreen: false,
        text: '正在重置设置并刷新...',
        spinner: 'el-icon-loading',
        background: 'rgba(0,0,0,0.5)'
      })
      localStorage.removeItem('layout-setting')
      setTimeout(() => window.location.reload(), 800)
    }
  }
}
</script>

<style lang="scss" scoped>
.mms-settings {
  padding: 20px;
  font-size: 13px;
  color: #374151;

  .mms-settings-section {
    margin-bottom: 4px;
  }

  .mms-settings-heading {
    font-size: 12px;
    font-weight: 600;
    color: #1a56db;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-bottom: 14px;
    padding-left: 8px;
    border-left: 3px solid #1a56db;
  }

  .mms-settings-divider {
    height: 1px;
    background: #f1f5f9;
    margin: 16px 0;
  }

  /* 主题卡片 */
  .mms-theme-row {
    display: flex;
    gap: 12px;
  }

  .mms-theme-card {
    position: relative;
    flex: 1;
    border: 2px solid #e5e7eb;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: border-color 0.2s, box-shadow 0.2s;

    &:hover { border-color: #93c5fd; }

    &.active {
      border-color: #1a56db;
      box-shadow: 0 0 0 2px rgba(26,86,219,0.12);
    }

    .mms-theme-preview {
      display: flex;
      height: 52px;

      .tp-sidebar {
        width: 26%;
        height: 100%;
      }

      .tp-content {
        flex: 1;
        height: 100%;
      }

      &.dark {
        .tp-sidebar { background: #0d1f4e; }
        .tp-content { background: #f1f5f9; }
      }

      &.light {
        .tp-sidebar { background: #eef2fb; border-right: 1px solid #dde6f8; }
        .tp-content { background: #f1f5f9; }
      }
    }

    .mms-theme-label {
      display: block;
      text-align: center;
      font-size: 12px;
      color: #6b7280;
      padding: 5px 0 6px;
    }

    .mms-theme-check {
      position: absolute;
      top: 5px;
      right: 6px;
      font-size: 13px;
      color: #1a56db;
      font-weight: bold;
    }
  }

  /* 主题色行 */
  .mms-color-row {
    display: flex;
    align-items: center;

    .mms-color-label {
      font-size: 13px;
      color: #6b7280;
    }
  }

  /* 开关行 */
  .mms-switch-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 0;
    border-bottom: 1px solid #f9fafb;

    &:last-child { border-bottom: none; }

    .mms-switch-info {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .mms-switch-name {
      font-size: 13px;
      color: #111827;
      font-weight: 500;
    }

    .mms-switch-desc {
      font-size: 11px;
      color: #9ca3af;
    }
  }

  /* 操作按钮 */
  .mms-settings-actions {
    display: flex;
    gap: 8px;

    .el-button { flex: 1; }
  }
}
</style>
