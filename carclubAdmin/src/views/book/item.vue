<template>
  <div class="app-container">
    <el-form :model="queryParams" class="main-search" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="会员ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入会员ID"
          clearable
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预约分类" prop="cateId">
        <el-select class="input" v-model="queryParams.cateId" clearable placeholder="请选择预约分类">
          <el-option
            v-for="item in cateList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
            :disabled="item.status !== 'A'"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="联系人" prop="contact">
        <el-input
          v-model="queryParams.contact"
          placeholder="请输入联系人姓名"
          clearable
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input
          v-model="queryParams.mobile"
          placeholder="请输入联系人手机号"
          clearable
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="状态"
          clearable
        >
          <el-option
            v-for="item in bookStatusList"
            :key="item.key"
            :label="item.name"
            :value="item.key"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table ref="tables" v-loading="loading" :data="list" @selection-change="handleSelectionChange" :default-sort="defaultSort" @sort-change="handleSortChange">
      <el-table-column label="ID" prop="id" width="80"/>
      <el-table-column label="会员ID" prop="userId"  width="100"/>
      <el-table-column label="预约项目" :show-overflow-tooltip="true" align="center" prop="bookId">
        <template slot-scope="scope">
          <span v-if="scope.row.bookName">{{ scope.row.bookName }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="手机号" align="center" prop="mobile">
        <template slot-scope="scope">
          <span v-if="scope.row.mobile">{{ scope.row.mobile }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="联系人" align="center" prop="contact">
        <template slot-scope="scope">
          <span v-if="scope.row.contact && scope.row.contact">
              <span>{{ scope.row.contact }}</span>
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="预约日期" align="center" prop="serviceDate">
         <template slot-scope="scope">
             <span v-if="scope.row.serviceDate">{{ parseTime(scope.row.serviceDate, '{y}-{m}-{d}') }}</span>
             <span v-else>-</span>
         </template>
      </el-table-column>
      <el-table-column label="预约时段" align="center" prop="serviceTime">
        <template slot-scope="scope">
          <span>{{ scope.row.serviceTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注信息" align="left" :show-overflow-tooltip="true" prop="remark">
        <template slot-scope="scope">
          <span v-if="scope.row.remark">{{ scope.row.remark }}</span>
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" prop="createTime">
        <template slot-scope="scope">
          <span v-if="scope.row.createTime">{{ parseTime(scope.row.createTime) }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <span>{{ scope.row.status ? getName(bookStatusList, scope.row.status) : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人" align="center" prop="createTime">
        <template slot-scope="scope">
          <span v-if="scope.row.operator">{{ scope.row.operator }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="170" fixed='right'>
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-success"
            v-if="scope.row.status == 'A'"
            v-hasPermi="['book:index']"
            @click="handleConfirm(scope.row)"
          >确认</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-error"
            v-hasPermi="['book:index']"
            v-if="scope.row.status == 'A'"
            @click="handleCancel(scope.row)"
          >取消</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-success"
            v-hasPermi="['book:index']"
            v-if="scope.row.status == 'B'"
            @click="completeCancel(scope.row)"
          >完成</el-button>
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
  </div>
</template>

<script>
import { getBookItemList, updateBookItemStatus } from "@/api/book";
export default {
  name: "BookItemList",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 预约分类
      cateList: [],
      // 默认排序
      defaultSort: {prop: 'createTime', order: 'descending'},
      // 状态列表
      bookStatusList: [],
      // 查询参数
      queryParams: {
        page: 1,
        pageSize: 10,
        mobile: '',
        userId: '',
        userNo: '',
        orderSn: '',
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
      getBookItemList(this.queryParams).then( response => {
          this.list = response.data.dataList.content;
          this.total = response.data.dataList.totalElements;
          this.bookStatusList = response.data.bookStatusList;
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
    handleSortChange(column, prop, order) {
      this.queryParams.orderByColumn = column.prop;
      this.queryParams.isAsc = column.order;
      this.getList();
    },
    // 删除预约项目
    handleDelete(row) {
      this.$modal.confirm('是否确认删除' + row.id + '"吗？').then(function() {
        return updateBookItemStatus(row.id, 'D');
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(function(e) {
        console.log(e)
      });
    },
    // 确认预约
    handleConfirm(row) {
      this.$modal.confirm('确认该预约审核通过吗？').then(function() {
        return updateBookItemStatus(row.id, 'B');
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("操作成功");
      }).catch(function(e) {
        console.log(e)
      });
    },
    // 完成预约
    completeCancel(row) {
      this.$modal.confirm('确认该预约已经完成吗？').then(function() {
        return updateBookItemStatus(row.id, 'E');
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("操作成功");
      }).catch(function(e) {
        console.log(e)
      });
    },
    // 取消预约
    handleCancel(row) {
      this.$modal.confirm('确认该预约审核不通过吗？').then(function() {
        return updateBookItemStatus(row.id, 'C');
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("操作成功");
      }).catch(function(e) {
        console.log(e)
      });
    },
  }
};
</script>

