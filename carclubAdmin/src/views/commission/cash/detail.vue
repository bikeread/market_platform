<template>
  <el-dialog
    class="common-dialog"
    title="佣金明细"
    :visible="showDialog"
    width="1200px"
    @close="cancel"
    destroy-on-close
  >
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
      <el-table-column prop="staff" label="会员名称" align="center" width="100">
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
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="page"
      :limit.sync="pageSize"
      @pagination="getCommissionLogList"
    />
  </el-dialog>
</template>
<script>
import { getCommissionLogList } from '@/api/commission/log'
export default {
  data() {
    return {
      loading: false,
      showDialog: false,
      uuid: [],
      list: [],
      page: 1, // 初始页
      pageSize: 10, // 每页的数据
      total: 0,
      // 状态列表
      statusList: [],
    }
  },
  methods: {
    init(uuid) {
      this.uuid = uuid
      this.showDialog = true
      this.getCommissionLogList()
    },
    getCommissionLogList() {
      let params = {
        page: this.page,
        pageSize: this.pageSize,
        uuid: this.uuid,
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
    cancel() {
      this.showDialog = false
    },
    // 订单详情
    handleView(orderId) {
      this.$router.push({ path: '/order/detail?orderId=' + orderId })
    },
  },
}
</script>
