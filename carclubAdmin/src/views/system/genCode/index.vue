<template>
  <div class="app-container">
    <el-form :model="queryParams" class="main-search" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="表名称" prop="title">
        <el-input
          v-model="queryParams.tableName"
          placeholder="请输入表名称"
          clearable
          style="width: 200px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="状态"
          clearable
          style="width: 100px"
        >
          <el-option key="A" label="启用" value="A"/>
          <el-option key="N" label="禁用" value="N"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:genCode:add']"
        >新增</el-button>
      </el-form-item>
    </el-form>

    <el-table ref="tables" v-loading="loading" :data="list" @selection-change="handleSelectionChange" :default-sort="defaultSort" @sort-change="handleSortChange">
      <el-table-column label="ID" prop="id" width="60"/>
      <el-table-column label="表名称" prop="tableName" />
      <el-table-column label="表前缀" prop="tablePrefix" />
      <el-table-column label="模块名称" prop="moduleName" />
      <el-table-column label="作者" align="center" prop="author" />
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="A"
            inactive-value="N"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            v-hasPermi="['system:genCode:add']"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-setting"
            v-hasPermi="['system:genCode:gen']"
            @click="doGenCode(scope.row)"
          >生成代码</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            v-hasPermi="['system:genCode:add']"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.page"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" class="common-dialog" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="表名称" prop="tableName">
              <el-input v-model="form.tableName" placeholder="请输入表名称" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="表前缀" prop="tablePrefix">
              <el-input v-model="form.tablePrefix" placeholder="请输入表前缀" maxlength="200"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="模块名称" prop="moduleName">
              <el-input v-model="form.moduleName" placeholder="请输入模块名称" maxlength="100"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="代码路径" prop="backendPath">
              <el-input v-model="form.backendPath" placeholder="请输入生成代码路径"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="作者" prop="author">
              <el-input v-model="form.author" placeholder="请输入作者"></el-input>
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
import { getGenCodeList, updateGenCodeStatus, getGenCodeInfo, saveGenCode, doGenCode } from "@/api/system/genCode";
export default {
  name: "GenCodeIndex",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 标题
      title: "",
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 图片根目录
      imagePath: "",
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 是否显示弹出层
      open: false,
      // 默认排序
      defaultSort: {prop: 'sort', order: 'ascending'},
      // 表单参数
      form: { id: '', tableName: '', tablePrefix: '', moduleName: '', author: '', backendPath: '',  status: "A" },
      // 店铺列表
      storeList: [],
      // 查询参数
      queryParams: {
        page: 1,
        pageSize: 10,
        tableName: '',
        status: ''
      },
      // 表单校验
      rules: {
        tableName: [
          { required: true, message: "表名称不能为空", trigger: "blur" },
          { min: 2, max: 200, message: '表名称长度必须介于2 和 200 之间', trigger: 'blur' }
        ],
        tablePrefix: [
          { required: true, message: "表前缀不能为空", trigger: "blur" },
          { min: 2, max: 200, message: '表前缀长度必须介于2 和 200 之间', trigger: 'blur' }
        ],
        moduleName: [
          { required: true, message: "模块名称不能为空", trigger: "blur" },
          { min: 2, max: 200, message: '模块名称长度必须介于2 和 200 之间', trigger: 'blur' }
        ],
        backendPath: [
          { required: true, message: "代码路径不能为空", trigger: "blur" },
          { min: 2, max: 200, message: '代码路径长度必须介于2 和 200 之间', trigger: 'blur' }
        ],
        author: [
          { required: true, message: "作者不能为空", trigger: "blur" },
          { min: 2, max: 200, message: '作者长度必须介于2 和 200 之间', trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 查询列表
    getList() {
      this.loading = true;
      getGenCodeList(this.queryParams).then( response => {
          this.list = response.data.dataList.content;
          this.total = response.data.dataList.totalElements;
          this.imagePath = response.data.imagePath;
          this.storeList = response.data.storeList;
          this.loading = false;
        }
      );
    },
    // 搜索按钮操作
    handleQuery() {
      this.queryParams.page = 1;
      this.getList();
    },
    // 重置按钮操作
    resetQuery() {
      this.resetForm("queryForm");
      this.$refs.tables.sort(this.defaultSort.prop, this.defaultSort.order);
      this.handleQuery();
    },
    // 状态修改
    handleStatusChange(row) {
      let text = row.status == "A" ? "启用" : "禁用";
      this.$modal.confirm('确认要' + text + '"' + row.title + '"吗？').then(function() {
        return updateGenCodeStatus(row.id, row.status);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status === "N" ? "A" : "N";
      });
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.multiple = !selection.length
    },
    // 排序触发事件
    handleSortChange(column) {
      this.queryParams.orderByColumn = column.prop;
      this.queryParams.isAsc = column.order;
      this.getList();
    },
    // 新增按钮操作
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "新增生成代码";
    },
    // 表单重置
    reset() {
      this.form = { id: '', tableName: '', tablePrefix: '', moduleName: '', author: '', backendPath: '',  status: "A" };
      this.resetForm("form");
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 提交按钮
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id) {
              saveGenCode(this.form).then(response => {
                this.$modal.msgSuccess("修改生成代码成功");
                this.open = false;
                this.getList();
              });
          } else {
              saveGenCode(this.form).then(response => {
                this.$modal.msgSuccess("新增生成代码成功");
                this.open = false;
                this.getList();
              });
          }
        }
      });
    },
    // 修改按钮操作
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getGenCodeInfo(id).then(response => {
        this.form = response.data.tGenCode;
        this.open = true;
        this.title = "编辑生成代码";
      });
    },
    // 删除按钮操作
    handleDelete(row) {
      const name = row.tableName;
      this.$modal.confirm('是否确认删除"' + name + '"的数据项？').then(function() {
        return updateGenCodeStatus(row.id, 'D');
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    // 生成按钮操作
    doGenCode(row) {
      this.$modal.confirm('确定生成"' + row.tableName + '"的代码？').then(function() {
        return doGenCode(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("代码生成成功");
      }).catch(() => {});
    }
  }
};
</script>
