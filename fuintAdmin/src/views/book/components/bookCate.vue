<template>
  <div class="cate-container">
    <el-dialog title="预约类别" :visible.sync="showDialog" class="common-dialog" width="950px" @close="close" destroy-on-close>
      <el-form ref="form" :model="form" label-width="120px" size="small" :inline="true">
        <el-row>
          <el-form-item label="类别名称" prop="name">
            <el-input
              v-model="form.name"
              placeholder="请输入类别名称"
              clearable
              style="width: 200px;"
              @keyup.enter.native="getCateList"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select
              v-model="form.status"
              placeholder="状态"
              clearable
            >
              <el-option key="A" label="启用" value="A"/>
              <el-option key="N" label="禁用" value="N"/>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="getCateList">查询</el-button>
            <el-button type="primary" icon="el-icon-plus" plain size="mini" @click="addCate">新增分类</el-button>
          </el-form-item>
        </el-row>
      </el-form>

      <el-table ref="tables" v-loading="loading" :data="cateList">
        <el-table-column label="ID" prop="id" width="80"/>
        <el-table-column label="类别名称" :show-overflow-tooltip="true" prop="name"/>
        <el-table-column label="图片" align="center" width="200">
          <template slot-scope="scope">
            <img class="list-img" :src="imagePath + scope.row.logo">
          </template>
        </el-table-column>
        <el-table-column label="排序" align="center" prop="sort">
          <template slot-scope="scope">
            <span>{{ scope.row.sort }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" align="center" prop="createTime">
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
        :page.sync="page"
        :limit.sync="pageSize"
        @pagination="getCateList"
      />
    </el-dialog>
    <bookCateForm :showDialog="openBookCate" :bookCateInfo="bookCateInfo" :imagePath="imagePath" :storeList="storeList" @closeDialog="closeDialog"></bookCateForm>
  </div>
</template>
<script>
import bookCateForm from "./bookCateForm";
import { getBookCateList, getBookCateInfo, updateBookCateStatus } from "@/api/book";
export default {
  name: "bookCate",
  components: {
    bookCateForm
  },
  props: {
    showDialog: {
      type:[Boolean],
      default:()=>false
    },
  },
  watch: {
    showDialog(value) {
      if (value) {
        this.getCateList();
      }
    }
  },
  data(){
    return {
      loading: false,
      openBookCate: false,
      bookCateInfo: null,
      page: 1, // 初始页
      pageSize: 10, // 每页数
      total: 0,
      imagePath: '',
      form: { page: 1, pageSize: 10 },
      storeList: [],
      cateList: [],
      statusList: []
    }
  },
  methods: {
    // 状态修改
    handleStatusChange(row) {
      let text = row.status == "A" ? "启用" : "禁用";
      this.$modal.confirm('确认要' + text + '"' + row.name + '"吗？').then(function() {
        return updateBookCateStatus(row.id, row.status);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status === "N" ? "A" : "N";
      });
    },
    // 获取分类列表
    getCateList() {
      const app = this;
      app.loading = true;
      getBookCateList(app.form).then( response => {
        if (response.data) {
            app.cateList = response.data.dataList.content;
            app.total = response.data.dataList.totalElements;
            app.imagePath = response.data.imagePath;
            app.storeList = response.data.storeList;
        }
        app.loading = false;
      }).catch((e) => {
         app.loading = false;
         console.log(e);
      });
    },
    // 新增分类
    addCate() {
      this.openBookCate = true;
      this.bookCateInfo = null;
    },
    // 修改分类
    handleUpdate(row) {
      const id = row.id || this.ids;
      this.bookCateInfo = null;
      getBookCateInfo(id).then(response => {
        this.bookCateInfo = response.data.bookCateInfo;
        this.openBookCate = true;
      });
    },
    // 删除分类
    handleDelete(row) {
      this.$modal.confirm('是否确认删除' + row.name + '"吗？').then(function() {
        return updateBookCateStatus(row.id, 'D');
      }).then(() => {
        this.getCateList();
        this.$modal.msgSuccess("删除成功");
      }).catch(function(e) {
         console.log(e)
      });
    },
    // 关闭对话框
    close() {
      this.$emit('closeDialog','cateDialog');
    },
    closeDialog() {
      this.openBookCate = false;
      this.getCateList();
    }
  }
}
</script>


