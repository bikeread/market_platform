<template>
  <div>
    <el-upload
      :action="uploadUrl"
      :before-upload="handleBeforeUpload"
      :on-success="handleUploadSuccess"
      :on-error="handleUploadError"
      name="file"
      :show-file-list="false"
      :headers="headers"
      :accept="uploadType === 'video' ? 'video/mp4,video/quicktime,video/x-m4v,video/x-msvideo' : 'image/*'"
      style="display: none"
      ref="upload"
      v-if="this.type == 'url'"
    >
    </el-upload>
    <div class="editor" ref="editor" :style="styles"></div>
  </div>
</template>

<script>
import Quill from "quill";
import "quill/dist/quill.core.css";
import "quill/dist/quill.snow.css";
import "quill/dist/quill.bubble.css";
import { getToken } from "@/utils/auth";

export default {
  name: "Editor",
  props: {
    /* 编辑器的内容 */
    value: {
      type: String,
      default: "",
    },
    /* 高度 */
    height: {
      type: Number,
      default: null,
    },
    /* 最小高度 */
    minHeight: {
      type: Number,
      default: null,
    },
    /* 只读 */
    readOnly: {
      type: Boolean,
      default: false,
    },
    // 上传文件大小限制(MB)
    fileSize: {
      type: Number,
      default: 100,
    },
    /* 类型（base64格式、url格式） */
    type: {
      type: String,
      default: "url",
    }
  },
  data() {
    return {
      uploadUrl: process.env.VUE_APP_SERVER_URL + 'backendApi/file/upload', // 上传的图片服务器地址
      headers: {
        'Access-Token': getToken()
      },
      uploadType: 'image', // 添加上传类型标识
      Quill: null,
      currentValue: "",
      options: {
        theme: "snow",
        bounds: document.body,
        debug: "warn",
        modules: {
          // 工具栏配置
          toolbar: [
            ["bold", "italic", "underline", "strike"],       // 加粗 斜体 下划线 删除线
            ["blockquote", "code-block"],                    // 引用  代码块
            [{ list: "ordered" }, { list: "bullet" }],       // 有序、无序列表
            [{ indent: "-1" }, { indent: "+1" }],            // 缩进
            [{ size: ["small", false, "large", "huge"] }],   // 字体大小
            [{ header: [1, 2, 3, 4, 5, 6, false] }],         // 标题
            [{ color: [] }, { background: [] }],             // 字体颜色、字体背景颜色
            [{ align: [] }],                                 // 对齐方式
            ["clean"],                                       // 清除文本格式
            ["link", "image", "video"]                       // 链接、图片、视频
          ],
        },
        placeholder: "请输入内容",
        readOnly: this.readOnly,
      },
    };
  },
  computed: {
    styles() {
      let style = {};
      if (this.minHeight) {
        style.minHeight = `${this.minHeight}px`;
      }
      if (this.height) {
        style.height = `${this.height}px`;
      }
      return style;
    },
  },
  watch: {
    value: {
      handler(val) {
        if (val !== this.currentValue) {
          this.currentValue = val === null ? "" : val;
          if (this.Quill) {
            this.Quill.pasteHTML(this.currentValue);
          }
        }
      },
      immediate: true,
    },
  },
  mounted() {
    this.init();
  },
  beforeDestroy() {
    this.Quill = null;
  },
  methods: {
    init() {
      const editor = this.$refs.editor;

      // 注册自定义视频 Blot
      const BlockEmbed = Quill.import('blots/block/embed');

      class VideoBlot extends BlockEmbed {
        static create(url) {
          const node = super.create();
          const video = document.createElement('video');
          video.src = url;
          video.setAttribute('controls', 'true');
          video.setAttribute('webkit-playsinline', 'true');
          video.setAttribute('playsinline', 'true');
          video.setAttribute('x5-video-player-type', 'h5');
          video.setAttribute('x5-video-player-fullscreen', 'true');
          video.className = 'ql-video';
          node.appendChild(video);
          return node;
        }

        static value(node) {
          return node.querySelector('video').src;
        }
      }

      VideoBlot.blotName = 'customVideo';
      VideoBlot.tagName = 'div';
      Quill.register(VideoBlot);

      // 初始化编辑器
      this.Quill = new Quill(editor, this.options);

      // 如果设置了上传地址则自定义图片和视频上传事件
      if (this.type == 'url') {
        let toolbar = this.Quill.getModule("toolbar");

        // 图片上传处理
        toolbar.addHandler("image", (value) => {
          this.uploadType = "image";
          if (value) {
            this.$refs.upload.$children[0].$refs.input.click();
          } else {
            this.Quill.format("image", false);
          }
        });

        // 添加视频上传处理
        toolbar.addHandler("video", (value) => {
          this.uploadType = "video";
          if (value) {
            this.$refs.upload.$children[0].$refs.input.click();
          } else {
            this.Quill.format("video", false);
          }
        });
      }

      this.Quill.pasteHTML(this.currentValue);
      this.Quill.on("text-change", (delta, oldDelta, source) => {
        const html = this.$refs.editor.children[0].innerHTML;
        const text = this.Quill.getText();
        const quill = this.Quill;
        this.currentValue = html;
        this.$emit("input", html);
        this.$emit("on-change", { html, text, quill });
      });
      this.Quill.on("text-change", (delta, oldDelta, source) => {
        this.$emit("on-text-change", delta, oldDelta, source);
      });
      this.Quill.on("selection-change", (range, oldRange, source) => {
        this.$emit("on-selection-change", range, oldRange, source);
      });
      this.Quill.on("editor-change", (eventName, ...args) => {
        this.$emit("on-editor-change", eventName, ...args);
      });
    },
    // 上传前校验格式和大小
    handleBeforeUpload(file) {
      console.log('Upload type:', this.uploadType, 'File:', file);

      // 根据上传类型判断大小限制
      const maxSize = this.uploadType === 'video' ? 100 : 100;

      // 校验文件大小
      const isLt = file.size / 1024 / 1024 <= maxSize;
      if (!isLt) {
        this.$message.error(`上传${this.uploadType === 'video' ? '视频' : '图片'}大小不能超过 ${maxSize}MB!`);
        return false;
      }

      // 如果是视频，校验格式
      if (this.uploadType === 'video') {
        const allowedTypes = [
          'video/mp4',      // MP4
          'video/quicktime', // MOV
          'video/x-m4v',    // M4V
          'video/x-msvideo' // AVI
        ];
        const isValidFormat = allowedTypes.includes(file.type);
        if (!isValidFormat) {
          this.$message.error('请上传 MP4、MOV、M4V 或 AVI 格式的视频！');
          return false;
        }
      }

      return true;
    },
    handleUploadSuccess(res, file) {
      console.log('Upload success:', res, file);

      let quill = this.Quill;

      if (res.code == 200) {
        try {
          let length = quill.getSelection()?.index || 0;

          if (this.uploadType === 'image') {
            quill.insertEmbed(length, "image", res.data.url);
          } else if (this.uploadType === 'video') {
            // 使用自定义的 customVideo blot
            quill.insertEmbed(length, "customVideo", res.data.url);
            quill.insertText(length + 1, '\n');
          }

          // 调整光标位置
          quill.setSelection(length + 2);
        } catch (error) {
          console.error('Error in handleUploadSuccess:', error);
          this.$message.error("处理上传响应时出错：" + error.message);
        }
      } else {
        const errorMsg = res.msg || '未知错误';
        this.$message.error(`${this.uploadType === 'video' ? '视频' : '图片'}上传失败: ${errorMsg}`);
      }
    },
    handleUploadError(err, file) {
      console.error('Upload error:', err, file);

      // 构建详细的错误信息
      let errorMessage = `${this.uploadType === 'video' ? '视频' : '图片'}上传失败\n`;

      // 添加文件信息
      if (file) {
        errorMessage += `文件名: ${file.name}\n`;
        errorMessage += `文件大小: ${(file.size / 1024 / 1024).toFixed(2)}MB\n`;
        errorMessage += `文件类型: ${file.type}\n`;
      }

      // 添加错误信息
      if (err) {
        if (err.status) {
          errorMessage += `状态码: ${err.status}\n`;
        }
        if (err.message) {
          errorMessage += `错误信息: ${err.message}\n`;
        }
        if (err.response) {
          try {
            const responseData = JSON.parse(err.response);
            errorMessage += `服务器响应: ${responseData.msg || JSON.stringify(responseData)}\n`;
          } catch (e) {
            errorMessage += `服务器响应: ${err.response}\n`;
          }
        }
      }

      // 使用 Element UI 的 Message Box 显示详细错误信息
      this.$alert(errorMessage, '上传失败', {
        confirmButtonText: '确定',
        type: 'error',
        dangerouslyUseHTMLString: false,
        customClass: 'upload-error-dialog'
      });
    },
  },
};
</script>

<style>
.editor, .selectGoods-toolbar {
  white-space: pre-wrap !important;
  line-height: normal !important;
}
.quill-img {
  display: none;
}
.ql-snow .ql-tooltip[data-mode="link"]::before {
  content: "请输入链接地址:";
}
.ql-snow .ql-tooltip.ql-editing a.ql-action::after {
  border-right: 0px;
  content: "保存";
  padding-right: 0px;
}

.ql-snow .ql-tooltip[data-mode="video"]::before {
  content: "请输入视频地址:";
}

.ql-snow .ql-picker.ql-size .ql-picker-label::before,
.ql-snow .ql-picker.ql-size .ql-picker-item::before {
  content: "14px";
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value="small"]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value="small"]::before {
  content: "10px";
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value="large"]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value="large"]::before {
  content: "18px";
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value="huge"]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value="huge"]::before {
  content: "32px";
}

.ql-snow .ql-picker.ql-header .ql-picker-label::before,
.ql-snow .ql-picker.ql-header .ql-picker-item::before {
  content: "文本";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="1"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="1"]::before {
  content: "标题1";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="2"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="2"]::before {
  content: "标题2";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="3"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="3"]::before {
  content: "标题3";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="4"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="4"]::before {
  content: "标题4";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="5"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="5"]::before {
  content: "标题5";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="6"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="6"]::before {
  content: "标题6";
}

.ql-snow .ql-picker.ql-font .ql-picker-label::before,
.ql-snow .ql-picker.ql-font .ql-picker-item::before {
  content: "标准字体";
}
.ql-snow .ql-picker.ql-font .ql-picker-label[data-value="serif"]::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value="serif"]::before {
  content: "衬线字体";
}
.ql-snow .ql-picker.ql-font .ql-picker-label[data-value="monospace"]::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value="monospace"]::before {
  content: "等宽字体";
}

.ql-editor video {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 10px auto;
  background: #000;
  border-radius: 4px;
}

/* 视频相关样式 */
.ql-editor .ql-video {
  width: 100%;
  max-width: 100%;
  height: auto;
  margin: 10px 0;
}

.ql-editor video {
  width: 100%;
  max-width: 100%;
  height: auto;
  display: block;
  margin: 0 auto;
  background: #000;
  border-radius: 4px;
}

/* 确保视频容器正确显示 */
.ql-editor p {
  margin: 0;
  padding: 0;
}

.ql-editor [data-video] {
  padding: 10px 0;
}

.upload-error-dialog .el-message-box__content {
  white-space: pre-line;
  word-break: break-all;
  max-height: 300px;
  overflow-y: auto;
}

.upload-error-dialog .el-message-box__message {
  font-family: monospace;
  font-size: 14px;
  line-height: 1.5;
}
</style>
