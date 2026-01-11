<template>
  <div class="qr-container">
    <el-dialog class="common-dialog" title="查看二维码" width="560px" :visible="showDialog" @close="close" destroy-on-close>
        <div class="dialog-qr" v-loading="loadQr">
          <div class="qr-detail" v-if="minAppQrCode">
            <div class="title">小程序：</div>
            <img class="qr-code" title="二维码" :src="minAppQrCode">
          </div>
          <div class="qr-detail" v-if="h5QrCode">
            <div class="title">h5：</div>
            <img class="qr-code" title="二维码" :src="h5QrCode">
          </div>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="close()">关闭</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import { createQrCode } from "@/api/common";
export default {
  name: 'FuintQrCode',
  props: {
    showDialog: {
      type:[Boolean],
      default:()=>false
    },
    qr: {
      type:[Object],
      default:()=>{}
    }
  },
  watch: {
    showDialog(value) {
      if (value) {
        this.confirm();
      }
    }
  },
  data(){
    return {
      loadQr: false,
      minAppQrCode: '',
      h5QrCode: ''
    }
  },
  methods: {
    confirm() {
      const app = this;
      const param = { type: this.qr.type, id: this.qr.id };
      app.loadQr = true;
      createQrCode(param).then( response => {
        if (response.data) {
            app.open = true;
            app.minAppQrCode = response.data.minAppQrCode;
            app.h5QrCode = response.data.h5QrCode;
            app.loadQr = false;
        } else {
            app.loadQr = false;
        }
      })
    },
    close() {
      this.$emit('closeDialog');
    }
  }
}
</script>
