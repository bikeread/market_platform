<template>
  <div class="app-container">
    <div class="main-panel">
      <el-form ref="form" :model="form" :rules="rules" label-width="240px" style="margin-top: 20px;">
        <el-row>
          <el-col :span="24">
            <el-form-item label="启用短信功能" prop="isClose">
              <el-select v-model="form.isClose" placeholder="启用短信功能">
                <el-option key="1" label="是" value="1"/>
                <el-option key="0" label="否" value="0"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="阿里云短信accessKeyId" prop="accessKeyId">
              <el-input v-model="form.accessKeyId" placeholder="阿里云短信accessKeyId" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="阿里云短信accessKeySecret" prop="accessKeySecret">
              <el-input v-model="form.accessKeySecret" placeholder="阿里云短信accessKeySecret" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="阿里云短信签名" prop="signName">
              <el-input v-model="form.signName" placeholder="阿里云短信签名" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getSmsSetting, saveSetting } from "@/api/smsManager";
export default {
  name: "SmsSetting",
  data() {
    return {
      // 遮罩层
      loading: false,
      // 表单参数
      form: { isClose: "0", accessKeyId: "", accessKeySecret: "", signName: "" },
      // 表单校验
      rules: {
        isClose: [
          { required: true, message: "请选择", trigger: "blur" },
        ]
      }
    };
  },
  created() {
    this.getSettingInfo();
  },
  methods: {
    // 查询配置
    getSettingInfo() {
      this.loading = true;
      getSmsSetting().then(response => {
          this.form = response.data;
          this.loading = false;
        }
      );
    },
    // 取消按钮
    cancel() {
      this.$store.dispatch('tagsView/delView', this.$route)
      this.$router.push('/')
    },
    // 提交按钮
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
            const param = { isClose: this.form.isClose,
                            accessKeyId: this.form.accessKeyId,
                            accessKeySecret: this.form.accessKeySecret,
                            signName: this.form.signName };
            saveSetting(param).then(response => {
               this.$modal.msgSuccess("保存成功");
               this.getSettingInfo();
            });
        }
      });
    }
  }
};
</script>
