<template>
  <div class="app-container">
    <el-form :model="queryParams" class="main-search" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="预约项目名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入预约项目名称"
          clearable
          style="width: 200px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预约类别" prop="cateId">
        <el-select
          v-model="queryParams.cateId"
          placeholder="预约类别"
          clearable
        >
          <el-option
            v-for="cate in cateList"
            :key="cate.id"
            :label="cate.name"
            :value="cate.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="状态"
          clearable
        >
          <el-option key="A" label="启用" value="A"/>
          <el-option key="N" label="禁用" value="N"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="primary" icon="el-icon-plus" plain size="mini" @click="addBook">新增预约项目</el-button>
        <el-button type="primary" icon="el-icon-menu" plain size="mini" @click="openCateDialog">预约分类管理</el-button>
      </el-form-item>
    </el-form>

    <el-table ref="tables" v-loading="loading" :data="list" @selection-change="handleSelectionChange" :default-sort="defaultSort" @sort-change="handleSortChange">
      <el-table-column label="项目ID" prop="id" width="80"/>
      <el-table-column label="预约类别" align="center" prop="cateId">
      <template slot-scope="scope">
          <span v-if="scope.row.cateId">
              <span>{{ getName(cateList, scope.row.cateId) }}</span>
          </span>
        <span v-else>无</span>
      </template>
      </el-table-column>
      <el-table-column label="图片" align="center" width="200">
        <template slot-scope="scope">
          <img class="list-img" :src="scope.row.logo">
        </template>
      </el-table-column>
      <el-table-column label="预约项目名称" align="center" :show-overflow-tooltip="true" prop="name">
        <template slot-scope="scope">
          <span v-if="scope.row.name && scope.row.name">{{ scope.row.name }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" :show-overflow-tooltip="true" prop="description">
        <template slot-scope="scope">
          <span v-if="scope.row.description">{{ scope.row.description }}</span>
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort">
        <template slot-scope="scope">
          <span>{{ scope.row.sort }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template slot-scope="scope">
          <span v-if="scope.row.createTime">{{ parseTime(scope.row.createTime) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime">
        <template slot-scope="scope">
          <span v-if="scope.row.updateTime">{{ parseTime(scope.row.updateTime) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人" align="center" prop="createTime">
        <template slot-scope="scope">
          <span v-if="scope.row.operator">{{ scope.row.operator }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            v-hasPermi="['book:index']"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            v-hasPermi="['book:index']"
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

    <!-- 预约对话框 start -->
    <bookForm :showDialog="open" :title="title" :form="form" :imagePath="imagePath" :storeList="storeList" :cateList="cateList" @closeDialog="closeDialog" @updateList="getList"></bookForm>
    <!-- 预约对话框 end -->

    <!-- 预约类别对话框 start -->
    <bookCate :showDialog="cateDialog" @closeDialog="closeDialog"></bookCate>
    <!-- 预约类别对话框 end -->
  </div>
</template>

<script>
import { getBookList, updateBookStatus, getBookInfo } from "@/api/book";
import bookForm from "./components/bookForm";
import bookCate from "./components/bookCate";
export default {
  name: "BookIndex",
  components: {
    bookForm,
    bookCate
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      title: '新增预约项目',
      // 新增编辑对话框
      open: false,
      // 新增编辑表单
      form: { name: '', description: '', storeId: '', sort: '', logo: '', status : 'A' },
      // 图片根目录
      imagePath: '',
      // 分类对话框
      cateDialog: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 店铺列表
      storeList: [],
      // 分类列表
      cateList: [],
      // 默认排序
      defaultSort: { prop: 'sort', order: 'ascending' },
      // 表单参数
      // 查询参数
      queryParams: {
        page: 1,
        pageSize: 10,
        name: '',
        cateId: '',
        status: ''
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
      getBookList(this.queryParams).then( response => {
          this.list = response.data.dataList.content;
          this.total = response.data.dataList.totalElements;
          this.storeList = response.data.storeList;
          this.imagePath = response.data.imagePath;
          this.cateList = response.data.cateList;
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
      this.dateRange = [];
      this.resetForm("queryForm");
      this.$refs.tables.sort(this.defaultSort.prop, this.defaultSort.order)
      this.handleQuery();
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
    // 新增预约
    addBook() {
      this.open = true;
      this.title = "新增预约项目";
      this.form = { name: '', description: '', storeId: '', sort: '', logo: '', status : 'A' };
    },
    // 修改预约
    handleUpdate(row) {
      const id = row.id || this.ids;
      getBookInfo(id).then(response => {
        this.form = response.data.bookInfo;
        this.open = true;
        this.title = "编辑预约项目";
      });
    },
    // 删除预约项目
    handleDelete(row) {
      this.$modal.confirm('是否确认删除' + row.name + '"吗？').then(function() {
        return updateBookStatus(row.id, 'D');
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(function(e) {
        console.log(e)
      });
    },
    // 状态修改
    handleStatusChange(row) {
      let text = row.status == "A" ? "启用" : "禁用";
      this.$modal.confirm('确认要' + text + '"' + row.name + '"吗？').then(function() {
        return updateBookStatus(row.id, row.status);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status === "N" ? "A" : "N";
      });
    },
    // 打开分类管理
    openCateDialog() {
      this.cateDialog = true;
    },
    // 关闭对话框
    closeDialog(dialog) {
      if (dialog == 'bookDialog') {
          this.open = false;
      } else {
          this.cateDialog = false;
      }
    }
  }
};
</script>

