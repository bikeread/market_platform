<template>
  <div class="app-container">
    <el-form
      class="main-search"
      ref="queryForm"
      size="small"
      :inline="true"
      label-width="68px"
    >
      <el-form-item>
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['content:navigation:edit']"
          >新增</el-button
        >
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="navigationList">
      <el-table-column label="序号" type="index" align="center">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="图片" align="center">
        <template slot-scope="scope">
          <img class="list-img" :src="imagePath + scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column
        label="导航名称"
        align="left"
        prop="name"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="排序" prop="sort"/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="A"
            inactive-value="N"
            @change="handleUpdateStatus(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['content:navigation:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleDelete(scope.row)"
            v-hasPermi="['content:navigation:edit']"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      class="common-dialog"
      width="700px"
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入导航名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="链接" prop="url">
              <el-input v-model="form.url" placeholder="请输入导航链接，格式如：pages/pay/index" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="9">
            <el-form-item label="图片" prop="icon">
              <el-upload
                :action="uploadAction"
                list-type="picture-card"
                :class="{ hide: hideUpload }"
                :file-list="uploadFiles"
                :auto-upload="true"
                :show-file-list="false"
                :headers="uploadHeader"
                :on-success="handleUploadSuccess"
              >
                <img
                  v-if="this.form.icon"
                  :src="imagePath + this.form.icon"
                  class="list-img"
                />
                <i v-if="!this.form.icon" class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
          </el-col>
          <p class="form-tips">（提示：点击图片修改，建议尺寸：90 x 90）</p>
        </el-row>
        <el-row>
          <el-col :span="14">
            <el-form-item label="排序" prop="sort">
              <el-input
                v-model="form.sort"
                placeholder="请输入排序数字，越大越靠前"
                maxlength="10"
              />
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
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getNavigation,
  saveNavigation
} from '@/api/navigation'
import { getToken } from '@/utils/auth'
export default {
  name: 'ContentNavigationIndex',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 图片根目录
      imagePath: '',
      // 上传地址
      uploadAction: process.env.VUE_APP_SERVER_URL + 'backendApi/file/upload',
      // 隐藏上传
      hideUpload: false,
      // 上传文件列表
      uploadFiles: [],
      uploadHeader: { 'Access-Token': getToken() },
      // 导航列表
      navigationList: [],
      // 店铺列表
      storeList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 表单参数
      form: {
        id: '',
        name: '',
        tips: '',
        url: '',
        icon: '',
        sort: 0,
        status: 'A'
      },
      // 表单校验
      rules: {
        name: [
          { required: true, message: '导航名称不能为空', trigger: 'blur' },
        ],
      },
    }
  },
  created() {
    this.getNavigation()
  },
  methods: {
    // 查询导航栏
    getNavigation() {
      this.loading = true
      getNavigation().then((response) => {
        this.navigationList = response.data.navigation;
        this.imagePath = response.data.imagePath;
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: '',
        name: '',
        icon: '',
        url: '',
        sort: 0
      }
      this.resetForm('form')
    },
    // 搜索按钮操作
    handleQuery() {
      this.getNavigation()
    },
    // 重置按钮操作
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 图片上传成功
    handleUploadSuccess(file) {
      this.form.icon = file.data.fileName
    },
    // 新增按钮操作
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增导航'
    },
    // 提交按钮
    submitForm: function () {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.form.icon.length < 1) {
              this.form.icon = '/static/defaultImage/none.png'
          }
          saveNavigation(this.form).then((response) => {
            this.$modal.msgSuccess('操作成功')
            this.open = false
            this.getNavigation()
          })
        }
      })
    },
    // 删除按钮操作
    handleDelete(row) {
      row.status = 'D';
      this.$modal
        .confirm('确定删除该导航吗？')
        .then(function () {
          return saveNavigation(row)
        })
        .then(() => {
          this.getNavigation()
          this.$modal.msgSuccess('删除成功')
        })
        .catch(() => {})
    },
    // 修改按钮操作
    handleUpdate(row) {
      this.reset();
      this.open = true;
      this.form = row;
      this.title = '修改导航';
    },
    // 状态修改
    handleUpdateStatus(row) {
      this.form = row;
      this.form.status = row.status;
      saveNavigation(this.form).then((response) => {
        this.$modal.msgSuccess('操作成功')
        this.open = false
        this.getNavigation()
      })
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
