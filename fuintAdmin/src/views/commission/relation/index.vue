<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      class="main-search"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="100px"
    >
      <el-form-item label="邀请会员ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入邀请会员ID"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="被邀请会员ID" prop="subUserId">
        <el-input
          v-model="queryParams.subUserId"
          placeholder="请输入被邀请会员ID"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属店铺" prop="store">
        <el-select
          v-model="queryParams.storeId"
          placeholder="所属店铺"
          clearable
          style="width: 180px"
        >
          <el-option
            :key="0"
            label="公共所有"
            v-if="!this.$store.getters.storeId"
            :value="0"
          />
          <el-option
            v-for="storeInfo in storeList"
            :key="storeInfo.id"
            :label="storeInfo.name"
            :value="storeInfo.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable>
          <el-option key="A" label="启用" value="A" />
          <el-option key="N" label="禁用" value="N" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>

    <el-table
      ref="tables"
      v-loading="loading"
      :data="list"
      @selection-change="handleSelectionChange"
      :default-sort="defaultSort"
      @sort-change="handleSortChange"
    >
      <el-table-column label="ID" prop="id" width="55" />
      <el-table-column label="邀请会员ID" align="center" prop="userInfo">
        <template slot-scope="scope">
          <p>{{ scope.row.userInfo.id }}</p>
        </template>
      </el-table-column>
      <el-table-column label="邀请会员号" align="center" prop="userInfo">
        <template slot-scope="scope">
          <p>{{ scope.row.userInfo.userNo }}</p>
        </template>
      </el-table-column>
      <el-table-column label="被邀请会员ID" align="center" prop="userInfo">
        <template slot-scope="scope">
          <p>{{ scope.row.subUserInfo.id }}</p>
        </template>
      </el-table-column>
      <el-table-column label="被邀请会员号" align="center" prop="subUserInfo">
        <template slot-scope="scope">
          <p>{{ scope.row.subUserInfo.userNo }}</p>
        </template>
      </el-table-column>
      <el-table-column label="等级" align="center" prop="level">
        <template slot-scope="scope">
          <p>{{ scope.row.level }}</p>
        </template>
      </el-table-column>
      <el-table-column label="邀请时间" align="center" prop="createTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
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
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            v-hasPermi="['commission:relation:index']"
            @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.page"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import {
  getCommissionRelationList,
  updateCommissionRelation,
  deleteCommissionRelation,
} from '@/api/commission/relation'
export default {
  name: 'CommissionRelationIndex',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 是否显示弹出层
      open: false,
      // 默认排序
      defaultSort: { prop: 'sort', order: 'ascending' },
      // 表单参数
      form: { id: '', userId: '', storeId: 0, status: 'A', subUserId: 0 },
      // 店铺列表
      storeList: [],
      // 查询参数
      queryParams: {
        page: 1,
        pageSize: 10,
        userId: '',
        subUserId: '',
        storeId: '',
        status: '',
      },
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询列表
    getList() {
      this.loading = true
      getCommissionRelationList(this.queryParams).then((response) => {
        this.list = response.data.dataList.content
        this.total = response.data.dataList.totalElements
        this.storeList = response.data.storeList
        this.loading = false
      })
    },
    // 搜索按钮操作
    handleQuery() {
      this.queryParams.page = 1
      this.getList()
    },
    // 重置按钮操作
    resetQuery() {
      this.resetForm('queryForm')
      this.$refs.tables.sort(this.defaultSort.prop, this.defaultSort.order)
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id)
      this.multiple = !selection.length
    },
    // 排序触发事件
    handleSortChange(column) {
      this.queryParams.orderByColumn = column.prop
      this.queryParams.isAsc = column.order
      this.getList()
    },
    // 表单重置
    reset() {
      this.form = {
        id: '',
        userId: '',
        status: 'A',
      }
      this.resetForm('form')
    },
    // 状态修改
    handleStatusChange(row) {
      let text = row.status == 'A' ? '启用' : '禁用'
      this.$modal
        .confirm('确认要' + text + '"' + row.id + '"吗？')
        .then(function () {
          return updateCommissionRelation(row.id, row.status)
        })
        .then(() => {
          this.$modal.msgSuccess(text + '成功')
        })
        .catch(function () {
          row.status = row.status === 'N' ? 'A' : 'N'
        })
    },
    // 删除按钮操作
    handleDelete(row) {
      const name = row.id
      this.$modal
        .confirm('是否确认删除ID="' + name + '"的数据项？')
        .then(function () {
          return deleteCommissionRelation(row.id, 'D')
        })
        .then(() => {
          this.getList()
          this.$modal.msgSuccess('删除成功')
        })
        .catch(() => {})
    },
  },
}
</script>
