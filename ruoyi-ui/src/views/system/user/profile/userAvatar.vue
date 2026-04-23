<template>
  <div>
    <!-- 头像展示 -->
    <div class="av-wrap" @click="editCropper">
      <img :src="options.img" class="av-img" alt="头像" />
      <div class="av-overlay">
        <i class="el-icon-camera"></i>
        <span>更换头像</span>
      </div>
    </div>

    <!-- 裁剪弹窗 -->
    <el-dialog
      title="更换头像"
      :visible.sync="open"
      width="760px"
      append-to-body
      @opened="modalOpened"
      @close="closeDialog"
      custom-class="av-dialog"
    >
      <div class="av-crop-area">
        <!-- 裁剪区 -->
        <div class="av-cropper-box">
          <vue-cropper
            v-if="visible"
            ref="cropper"
            :img="options.img"
            :info="false"
            :autoCrop="true"
            :autoCropWidth="200"
            :autoCropHeight="200"
            :fixedBox="true"
            outputType="png"
            @realTime="realTime"
          />
        </div>

        <!-- 预览区 -->
        <div class="av-preview-box">
          <div class="av-preview-title">预览效果</div>
          <div class="av-preview-circle">
            <img v-if="previews.url" :src="previews.url" :style="previews.img" />
          </div>
          <div class="av-preview-label">头像预览</div>
        </div>
      </div>

      <!-- 工具栏 -->
      <div class="av-toolbar">
        <div class="av-toolbar-left">
          <el-upload action="#" :http-request="requestUpload" :show-file-list="false" :before-upload="beforeUpload">
            <el-button size="small" icon="el-icon-folder-opened">选择图片</el-button>
          </el-upload>
          <el-button size="small" icon="el-icon-zoom-in"       @click="changeScale(1)"   title="放大"></el-button>
          <el-button size="small" icon="el-icon-zoom-out"      @click="changeScale(-1)"  title="缩小"></el-button>
          <el-button size="small" icon="el-icon-refresh-left"  @click="rotateLeft"       title="左转"></el-button>
          <el-button size="small" icon="el-icon-refresh-right" @click="rotateRight"      title="右转"></el-button>
        </div>
        <div class="av-toolbar-right">
          <el-button @click="open = false">取消</el-button>
          <el-button type="primary" icon="el-icon-upload2" @click="uploadImg">上传头像</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import store from "@/store";
import { VueCropper } from "vue-cropper";
import { uploadAvatar } from "@/api/system/user";

export default {
  components: { VueCropper },
  props: { user: { type: Object } },
  data() {
    return {
      open: false,
      visible: false,
      options: { img: store.getters.avatar },
      previews: {}
    };
  },
  methods: {
    editCropper()  { this.open = true; },
    modalOpened()  { this.visible = true; },
    requestUpload() {},
    rotateLeft()   { this.$refs.cropper.rotateLeft(); },
    rotateRight()  { this.$refs.cropper.rotateRight(); },
    changeScale(n) { this.$refs.cropper.changeScale(n || 1); },
    realTime(data) { this.previews = data; },
    closeDialog()  {
      this.options.img = store.getters.avatar;
      this.visible = false;
    },
    beforeUpload(file) {
      if (!file.type.startsWith("image/")) {
        this.msgError("请上传图片文件（JPG / PNG）");
        return false;
      }
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => { this.options.img = reader.result; };
    },
    uploadImg() {
      this.$refs.cropper.getCropBlob(data => {
        const fd = new FormData();
        fd.append("avatarfile", data);
        uploadAvatar(fd).then(res => {
          this.open = false;
          this.visible = false;
          this.options.img = process.env.VUE_APP_BASE_API + res.imgUrl;
          store.commit("SET_AVATAR", this.options.img);
          this.msgSuccess("头像更新成功");
        });
      });
    }
  }
};
</script>

<style lang="scss" scoped>
/* ── 头像入口 ── */
.av-wrap {
  position: relative;
  width: 90px;
  height: 90px;
  margin: 0 auto;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;

  &:hover .av-overlay { opacity: 1; }
}

.av-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  border: 3px solid #e8eef8;
  box-shadow: 0 2px 10px rgba(0,0,0,.10);
  display: block;
}

.av-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, .50);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  opacity: 0;
  transition: opacity .2s;

  i    { font-size: 20px; color: #fff; }
  span { font-size: 11px; color: rgba(255,255,255,.9); }
}

/* ── 裁剪弹窗内容 ── */
.av-crop-area {
  display: flex;
  gap: 20px;
  height: 320px;
}

.av-cropper-box {
  flex: 1;
  border-radius: 8px;
  overflow: hidden;
  background: #1a1a2e;
}

.av-preview-box {
  width: 140px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.av-preview-title {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
  align-self: flex-start;
}

.av-preview-circle {
  width: 110px;
  height: 110px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #e8eef8;
  box-shadow: 0 2px 12px rgba(0,0,0,.12);
  background: #f1f5f9;
  flex-shrink: 0;
}

.av-preview-label {
  font-size: 11px;
  color: #9ca3af;
}

/* ── 工具栏 ── */
.av-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid #f1f5f9;

  .av-toolbar-left {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .av-toolbar-right {
    display: flex;
    gap: 8px;
  }
}
</style>
