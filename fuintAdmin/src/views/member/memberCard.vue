<template>
  <!-- 微信会员卡设置对话框 -->
  <el-dialog title="微信会员卡设置" :visible.sync="showDialog" class="common-dialog" width="800px" @close="cancel" destroy-on-close>
    <el-form ref="cardForm" :model="cardForm" :rules="rules" label-width="180px">
      <el-row>
        <el-col :span="24">
          <el-form-item label="商户名称：" prop="brandName">
            <el-input v-model="cardForm.brandName" placeholder="请输入变更数量" maxlength="100" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="9">
          <el-form-item label="商户logo：" prop="logoUrl">
            <el-upload
              :action="uploadAction"
              list-type="picture-card"
              :class="{ hide:hideUpload }"
              :file-list="uploadFiles"
              :auto-upload="true"
              :show-file-list="false"
              :headers="uploadHeader"
              :on-success="handleUploadLogoSuccess">
              <img
                v-if="this.cardForm.logoUrl"
                :src="imagePath + this.cardForm.logoUrl"
                class="list-img"
              />
              <i v-if="!this.cardForm.logoUrl" class="el-icon-plus"></i>
            </el-upload>
          </el-form-item>
        </el-col>
        <p class="form-tips">（提示：点击图片修改，建议尺寸：300 x 300）</p>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="会员卡名称：" prop="title">
            <el-input v-model="cardForm.title" placeholder="请输入会员卡名称" maxlength="100" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="显示积分：" prop="supplyBonus">
            <el-radio-group v-model="cardForm.supplyBonus">
              <el-radio :key="true" :label="true" :value="true">是</el-radio>
              <el-radio :key="false" :label="false" :value="false">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="跳转外链查看积分URL：" prop="bonusUrl">
            <el-input v-model="cardForm.bonusUrl" placeholder="请输入跳转外链查看积分URL" maxlength="255" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="积分规则：" prop="bonusRules">
            <el-input v-model="cardForm.bonusRules" placeholder="请输入积分规则" maxlength="100" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="是否支持储值：" prop="supplyBalance">
            <el-radio-group v-model="cardForm.supplyBalance">
              <el-radio :key="true" :label="true" :value="true">是</el-radio>
              <el-radio :key="false" :label="false" :value="false">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="跳转外链查看余额URL：" prop="balanceUrl">
            <el-input v-model="cardForm.balanceUrl" placeholder="请输入跳转外链查看余额URL" maxlength="255" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="会员卡颜色：" prop="color">
            <el-input v-model="cardForm.color" placeholder="请输入会员卡颜色，填写Color010-Color100" maxlength="100" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="卡券使用提醒：" prop="notice">
            <el-input v-model="cardForm.notice" placeholder="请输入卡券使用提醒，字数上限为16个汉字" maxlength="100" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="客服电话：" prop="servicePhone">
            <el-input v-model="cardForm.servicePhone" placeholder="请输入客服电话" maxlength="100" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="跳转外链的入口名字：" prop="customUrlName">
            <el-input v-model="cardForm.customUrlName" placeholder="请输入跳转外链的入口名字" maxlength="100" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="跳转外链的URL：" prop="customUrl">
            <el-input v-model="cardForm.customUrl" placeholder="请输入跳转外链的URL" maxlength="255" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="显示在入口右侧的提示语：" prop="customUrlSubTitle">
            <el-input v-model="cardForm.customUrlSubTitle" placeholder="请输入显示在入口右侧的提示语" maxlength="100" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="卡券领取页面是否可分享：" prop="canShare">
            <el-radio-group v-model="cardForm.canShare">
              <el-radio :key="true" :label="true" :value="true">是</el-radio>
              <el-radio :key="false" :label="false" :value="false">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item class="recharge-item" prop="prerogative" label="会员卡特权说明：">
            <el-input v-model="cardForm.prerogative" type="textarea" rows="2" placeholder="请输入会员卡特权说明，字数上限为1024个汉字" maxlength="255" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item class="recharge-item" prop="description" label="卡券使用说明：">
            <el-input v-model="cardForm.description" type="textarea" rows="4" placeholder="请输入卡券使用说明，字数上限为1024个汉字" maxlength="255" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitCardForm">确定</el-button>
      <el-button @click="cancelCardForm">取消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getMemberSetting, saveSetting } from "@/api/member";
import { getToken } from '@/utils/auth';
export default {
  name: "memberCard",
  props: {
    showDialog:{
      type:[Boolean],
      default:()=>false
    }
  },
  data() {
    return {
      // 遮罩层
      loading: false,
      memberInfo: { name: '', id: '', point: 0 },
      // 表单参数
      cardForm: { cardType: 'MEMBER_CARD', brandName: '', backgroundUrl: '', logoUrl: '', title: '', color: 'Color010', notice: '', supplyBonus: true,
                  servicePhone: '', customUrlName: '', customUrl: '', customUrlSubTitle: '', canShare: true, prerogative: '',
                  bonusUrl: '', bonusRules: '', supplyBalance: 'false', balanceUrl: '' },
      // 图片根目录
      imagePath: "",
      // 上传地址
      uploadAction: process.env.VUE_APP_SERVER_URL + 'backendApi/file/upload',
      // 隐藏上传
      hideUpload: false,
      // 上传文件列表
      uploadFiles: [],
      uploadHeader: { 'Access-Token' : getToken() },
      // 表单校验
      rules: {
        brandName: [
          { required: true, message: "请输入商户名称", trigger: "blur" },
        ],
        title: [
          { required: true, message: "请输入卡券名称", trigger: "blur" },
        ],
        supplyBonus: [
          { required: true, message: "请选择是否显示积分", trigger: "blur" },
        ],
        logoUrl: [{ required: true, message: "请上传图片", trigger: "blur" }],
        color: [{ required: true, message: "请填写会员卡颜色，按色彩规范填写", trigger: "blur" }],
        notice: [{ required: true, message: "请填写卡券使用提醒，字数上限为16个汉字", trigger: "blur" }],
        prerogative: [{ required: true, message: "会员卡特权说明，限制1024汉字", trigger: "blur" }],
        description: [{ required: true, message: "卡券使用说明，字数上限为1024个汉字", trigger: "blur" }],
      }
    };
  },
  watch: {
    showDialog(value) {
      if (value) {
          this.getMemberSetting();
      }
    }
  },
  methods: {
    // 查询会员卡信息
    getMemberSetting() {
      this.loading = true;
      getMemberSetting().then(response => {
          if (response.data.wxMemberCard) {
              this.cardForm = response.data.wxMemberCard;
          }
          this.imagePath = response.data.imagePath;
          this.loading = false;
        }
      );
    },
    // 取消按钮
    cancelCardForm() {
      this.$emit('closeDialog','memberCard');
    },
    // 重置表单
    reset() {
      this.cardForm = { cardType: 'MEMBER_CARD', brandName: '', backgroundUrl: '', logoUrl: '', title: '', color: '', notice: '', supplyBonus: 'true',
        servicePhone: '', customUrlName: '', customUrl: '', customUrlSubTitle: '', canShare: 'true', prerogative: '',
        bonusUrl: '', bonusRules: '', supplyBalance: 'false', balanceUrl: '' };
    },
    // 提交按钮
    submitCardForm: function() {
      this.$refs["cardForm"].validate(valid => {
        if (valid) {
           const jsonStr = JSON.stringify(this.cardForm);
           saveSetting({ wxMemberCard: jsonStr }).then(response => {
               this.$alert("设置会员卡成功！");
               this.$emit('closeDialog','memberCard');
               this.reset();
            });
        }
      });
    },
    // 背景图上传处理
    handleUploadSuccess(file) {
      this.cardForm.backgroundUrl = file.data.fileName;
    },
    // 商户logo图上传处理
    handleUploadLogoSuccess(file) {
      this.cardForm.logoUrl = file.data.fileName;
    }
  }
};
</script>
<style scoped>
.common-dialog >>> .el-upload--picture-card {
  width: 60px;
  height: 50px;
  line-height: 60px;
}
</style>
