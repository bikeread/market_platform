<template>
  <div class="app-container">
    <!--查询参数-->
    <el-form size="small" :inline="true" class="main-search" label-width="68px">
      <el-form-item label="分佣对象" prop="target">
        <el-select
          v-model="target"
          placeholder="请选择方案对象"
          clearable
          style="width: 100%"
        >
          <el-option key="" label="全部" value="" />
          <el-option key="staff" label="员工提成" value="staff" />
          <el-option key="member" label="会员分销" value="member" />
        </el-select>
      </el-form-item>
      <el-form-item label="所属店铺" prop="storeId">
        <el-select
          v-model="storeId"
          filterable
          clearable
          placeholder="请选择店铺"
          style="width: 100%"
        >
          <el-option
            v-for="item in storeOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="status"
          placeholder="请选择状态"
          clearable
          style="width: 100%"
        >
          <el-option key="" label="全部" value="" />
          <el-option
            v-for="item in statusList"
            :key="item.key"
            :label="item.name"
            :value="item.key"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="员工姓名">
        <el-input
          v-model="realName"
          placeholder="请输入员工姓名"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="员工手机">
        <el-input
          v-model="mobile"
          placeholder="请输入员工手机号"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="起止时间">
        <el-date-picker
          v-model="startTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetime"
          style="width: 200px"
          placeholder="开始时间"
        ></el-date-picker>
        <span class="sp"> ~ </span>
        <el-date-picker
          v-model="endTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetime"
          style="width: 200px"
          placeholder="结束时间"
        ></el-date-picker>
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
        <el-button
          icon="el-icon-plus"
          type="primary"
          plain
          size="mini"
          @click="doSettle"
          >发起结算</el-button
        >
      </el-form-item>
    </el-form>

    <el-table
      ref="tables"
      v-loading="loading"
      :data="list"
      size="medium"
      style="width: 100%"
    >
      <el-table-column prop="orderId" label="订单号" width="190">
        <template slot-scope="scope">
          <span
            @click="handleView(scope.row.orderInfo.id)"
            style="color: #00afff; cursor: pointer"
            >{{ scope.row.orderInfo.orderSn }}</span
          >
        </template>
      </el-table-column>
      <el-table-column
        prop="target"
        label="分佣对象"
        align="center"
        width="100"
      >
        <template slot-scope="scope">
          <span>{{ getName(targetList, scope.row.target) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="staff" label="员工姓名" align="center" width="100">
        <template slot-scope="scope">
          <span>{{
            scope.row.staffInfo ? scope.row.staffInfo.realName : '-'
          }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="userName"
        label="会员名称"
        align="center"
        width="100"
      >
        <template slot-scope="scope">
          <span>{{ scope.row.userInfo ? scope.row.userInfo.name : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="typeName" label="订单类型"></el-table-column>
      <el-table-column
        prop="storeName"
        :show-overflow-tooltip="true"
        label="开单门店"
      >
        <template slot-scope="scope">
          <span>{{ scope.row.storeInfo.name }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="orderDate" label="开单时间" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.orderInfo.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="分佣金额">
        <template slot-scope="scope">
          <span>{{ scope.row.amount.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="comType"
        :show-overflow-tooltip="true"
        label="规则方案"
      >
        <template slot-scope="scope">
          <span>{{ scope.row.ruleInfo.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <span>{{ getName(statusList, scope.row.status) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="时间" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['commission:rule:index']"
            >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleCancel(scope.row)"
            :disabled="scope.row.status != 'A'"
            v-hasPermi="['commission:rule:index']"
            >作废
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="page"
      :limit.sync="pageSize"
      @pagination="getCommissionLogList"
    />

    <!-- 添加或修改对话框 -->
    <el-dialog
      title="修改佣金"
      :visible.sync="openEdit"
      class="common-dialog"
      width="700px"
      append-to-body
    >
      <el-form ref="form" :model="updateForm" label-width="120px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="订单ID" prop="orderId">
              <el-input v-model="updateForm.orderId" disabled maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="分佣金额" prop="amount">
              <el-input
                v-model="updateForm.amount"
                placeholder="请输入分佣金额"
                maxlength="200"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注信息">
              <el-input
                v-model="updateForm.description"
                type="textarea"
                placeholder="请输入内容"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="状态">
              <el-radio-group v-model="updateForm.status">
                <el-radio key="A" label="A" value="A">正常</el-radio>
                <el-radio key="C" label="C" value="C">作废</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitUpdate">确定</el-button>
        <el-button @click="cancelUpdate">取消</el-button>
      </div>
    </el-dialog>

    <!-- 结算对话框 -->
    <el-dialog
      title="结算确认"
      :visible.sync="settleDialog"
      class="common-dialog"
      width="700px"
      append-to-body
    >
      <el-table ref="tables" v-loading="loading" :data="cashList">
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column
          prop="mobile"
          label="手机号"
          align="center"
          width="120"
        >
          <template slot-scope="scope">
            <span>{{
              scope.row.staffInfo ? scope.row.staffInfo.mobile : '-'
            }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="staff"
          label="员工姓名"
          align="center"
          width="100"
        >
          <template slot-scope="scope">
            <span>{{
              scope.row.staffInfo ? scope.row.staffInfo.realName : '-'
            }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="staff"
          label="会员名称"
          :show-overflow-tooltip="true"
          align="center"
          width="100"
        >
          <template slot-scope="scope">
            <span>{{
              scope.row.userInfo ? scope.row.userInfo.name : '-'
            }}</span>
          </template>
        </el-table-column>
        <el-table-column label="所属店铺" align="center" prop="storeInfo.name">
          <template slot-scope="scope">
            <span v-if="scope.row.storeInfo">{{
              scope.row.storeInfo.name
            }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column
          label="结算佣金"
          align="center"
          width="100"
          prop="amount"
        >
          <template slot-scope="scope">
            <span v-if="true">{{ scope.row.amount.toFixed(2) }}</span>
            <span v-else>0.00</span>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="totalCash > 0"
        :total="totalCash"
        :page.sync="page"
        :limit.sync="pageSize"
        @pagination="queryCashList"
      />
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitSettle">确定结算</el-button>
        <el-button @click="cancelSettle">取消结算</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {
  getCommissionLogList,
  updateCommissionLog,
  deleteCommissionLog,
  doSettle,
} from '@/api/commission/log'
import {
  getCashList,
  confirmCommissionCash,
  cancelCommissionCash,
} from '@/api/commission/cash'
import { searchStore } from '@/api/store'
export default {
  name: 'CommissionLogIndex',
  data() {
    return {
      loading: false,
      // 发起结算对话框
      settleDialog: false,
      cashList: [],
      totalCash: 0,
      // 是否显示弹出层
      openEdit: false,
      updateForm: {},
      // 状态列表
      statusList: [],
      // 分佣对象
      targetList: [],
      page: 1,
      pageSize: 10,
      total: 0,
      list: [],
      storeOptions: [],
      realName: '',
      mobile: '',
      storeId: '',
      status: '',
      target: '',
      startTime: null,
      endTime: null,
      openStaffScheme: false,
      detailDialog: false,
      uuid: '',
    }
  },
  methods: {
    getCommissionLogList() {
      let params = {
        page: this.page,
        pageSize: this.pageSize,
        target: this.target,
        realName: this.realName,
        mobile: this.mobile,
        storeId: this.storeId,
        status: this.status,
        startTime: this.startTime,
        endTime: this.endTime,
      }
      this.loading = true
      getCommissionLogList(params).then((response) => {
        this.list = response.data.dataList.content
        this.total = response.data.dataList.totalElements
        this.statusList = response.data.statusList
        this.targetList = response.data.targetList
        this.loading = false
      })
    },
    handleQuery() {
      this.getCommissionLogList()
    },
    // 重置按钮操作
    resetQuery() {
      this.page = 1
      this.mobile = ''
      this.storeId = ''
      this.status = ''
      this.realName = ''
      this.startTime = ''
      this.endTime = ''
      this.handleQuery()
    },
    // 修改按钮
    handleUpdate(row) {
      this.openEdit = true
      this.updateForm = row
    },
    // 作废按钮
    handleCancel(row) {
      const app = this
      app
        .$confirm('您确定要作废该笔佣金吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
        .then(() => {
          deleteCommissionLog(row.id).then((response) => {
            if (response.code == '200') {
              app.$modal.msgSuccess('操作成功！')
              app.getCommissionLogList()
            } else {
              app.$modal.msgError('操作失败!')
            }
          })
        })
        .catch(() => {})
    },
    // 确定修改
    submitUpdate() {
      updateCommissionLog(this.updateForm)
        .then((response) => {
          this.$modal.msgSuccess('提交成功')
          this.updateForm = {}
          this.openEdit = false
          this.getCommissionLogList()
        })
        .catch(function () {
          this.$modal.msgError('提交失败')
        })
    },
    // 取消修改
    cancelUpdate() {
      this.updateForm = {}
      this.openEdit = false
    },
    // 发起结算
    doSettle() {
      const app = this
      app
        .$confirm('您确定要发起结算吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
        .then(() => {
          let params = {
            target: this.target,
            realName: this.realName,
            mobile: this.mobile,
            storeId: this.storeId,
            startTime: this.startTime,
            endTime: this.endTime,
          }
          doSettle(params).then((response) => {
            if (response.code == '200') {
              app.$modal.msgSuccess('操作成功！')
              app.uuid = response.data
              app.queryCashList()
            } else {
              app.$modal.msgError('操作失败!')
            }
          })
        })
        .catch(() => {})
    },
    // 提交结算
    submitSettle() {
      this.settleDialog = false
      confirmCommissionCash({ uuid: this.uuid })
        .then((response) => {
          this.getCommissionLogList()
          this.settleDialog = false
          this.$modal.msgSuccess('确认成功！')
        })
        .catch(function () {
          this.$modal.msgError('确认失败')
        })
    },
    // 取消结算
    cancelSettle() {
      this.settleDialog = false
      cancelCommissionCash({ uuid: this.uuid })
        .then((response) => {
          this.getCommissionLogList()
          this.settleDialog = false
          this.$modal.msgSuccess('取消成功！')
        })
        .catch(function () {
          this.$modal.msgError('取消结算')
        })
    },
    // 查询结算列表
    queryCashList() {
      this.loading = true
      getCashList({ uuid: this.uuid }).then((response) => {
        this.cashList = response.data.dataList.content
        this.totalCash = response.data.dataList.totalElements
        this.statusList = response.data.statusList
        this.settleDialog = true
        this.loading = false
      })
    },
    // 店铺列表
    getStoreList() {
      searchStore().then((response) => {
        this.storeOptions = response.data.storeList
      })
    },
    // 订单详情
    handleView(orderId) {
      this.$router.push({ path: '/order/detail?orderId=' + orderId })
    },
  },
  created: function () {
    this.getCommissionLogList()
    this.getStoreList()
  },
}
</script>
<style scoped>
select {
  /*去除select边框*/
  border: 0;
  background: transparent;
  /*去除下拉框的三角下标*/
  appearance: none;
  -moz-appearance: none; /* Firefox */
  -webkit-appearance: none; /* Safari 和 Chrome */
  height: 32px;
  width: 100%;
  background-color: white;
  padding-left: 5px;
  border-radius: 5px;
}

input {
  width: 96%;
  height: 32px;
  font-size: 14px;
  color: #686868;
  border: 1px solid #d3d6dd;
  padding-left: 5px;
  border-radius: 5px;
}

.queryInput >>> .el-input__inner {
  border-radius: 0px;
  width: 200px;
}
</style>
