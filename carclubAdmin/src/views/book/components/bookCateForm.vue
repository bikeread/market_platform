<template>
  <!-- 添加或修改分类对话框 -->
  <el-dialog :title="title" :visible.sync="showDialog" class="common-dialog" width="700px" :before-close="handleCLose" @close="handleCLose" destroy-on-close>
    <el-form ref="form" :model="form" :rules="rules" label-width="120px">
      <el-row>
        <el-col :span="24">
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="200" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="所属店铺" prop="storeId">
            <el-select
              v-model="form.storeId"
              style="width: 260px"
              placeholder="所属店铺，空则为公共所有"
            >
              <el-option :key="0" label="公共所有" v-if="!this.$store.getters.storeId" :value="0"/>
              <el-option v-for="storeInfo in storeList" :key="storeInfo.id" :label="storeInfo.name" :value="storeInfo.id"/>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="9">
          <el-form-item label="图片" prop="logo">
            <el-upload
              :action="uploadAction"
              list-type="picture-card"
              :class="{hide:hideUpload}"
              :file-list="uploadFiles"
              :auto-upload="true"
              :show-file-list="false"
              :headers="uploadHeader"
              :on-success="handleUploadSuccess">
              <img
                v-if="this.form.logo"
                :src="imagePath + this.form.logo"
                class="list-img"
              />
              <i v-if="!this.form.logo" class="el-icon-plus"></i>
            </el-upload>
          </el-form-item>
        </el-col>
        <p class="form-tips">（提示：点击图片修改，建议尺寸：200 x 140）</p>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="排序" prop="sort">
            <el-input-number v-model="form.sort" :min="0"/>
            <div class="form-tips">提示：数值越小，排行越靠前</div>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="备注信息">
            <el-input v-model="form.description" type="textarea" placeholder="请输入内容"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio key="A" label="A" value="A">启用</el-radio>
              <el-radio key="N" label="N" value="N">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="doSubmit">确定</el-button>
      <el-button @click="handleCLose">取消</el-button>
    </div>
  </el-dialog>
</template>
<script>
import { saveBookCate } from "@/api/book";
import { getToken } from '@/utils/auth';
export default {
  name: "bookCateForm",
  props: {
    showDialog: {
      type:[Boolean],
      default:()=>false
    },
    imagePath: {
      type:[String],
      default:()=>''
    },
    storeList: {
      type:[Array],
      default:()=>[]
    },
    bookCateInfo: {
      type:[Object],
      default:()=>null
    },
  },
  watch: {
    showDialog(value) {
      if (value && this.bookCateInfo) {
        this.form = this.bookCateInfo;
      } else {
        this.form = { name: '', description: '', storeId: '', sort: '', logo: '', status : 'A' };
      }
    }
  },
  data(){
    return {
      loading: false,
      total: 0,
      title: '新增预约分类',
      form: { name: '', description: '', storeId: '', sort: '', logo: '', status : 'A' },
      // 上传地址
      uploadAction: process.env.VUE_APP_SERVER_URL + 'backendApi/file/upload',
      // 隐藏上传
      hideUpload: false,
      // 上传文件列表
      uploadFiles: [],
      uploadHeader: { 'Access-Token' : getToken() },
      rules: {
        name: [
          { required: true, message: "名称不能为空", trigger: "blur" },
          { min: 2, max: 200, message: '名称长度必须介于2 和 200 之间', trigger: 'blur' }
        ],
        logo: [{ required: true, message: "请上传图片", trigger: "blur" }]
      }
    }
  },
  methods: {
    // 提交
    doSubmit() {
      const app = this;
      const param = app.form;
      saveBookCate(param).then( response => {
        if (response) {
           app.$modal.msgSuccess("提交成功！");
           app.handleCLose();
        }
      })
    },
    // 关闭对话框
    handleCLose() {
      this.$emit('closeDialog', 'bookCateDialog');
    },
    // 上传成功回调
    handleUploadSuccess(file) {
      this.form.logo = file.data.fileName;
    }
  }
}
</script>
<style scoped>
.common-dialog >>> .el-upload--picture-card {
  width: 60px;
  height: 50px;
  line-height: 60px;
}
</style>

