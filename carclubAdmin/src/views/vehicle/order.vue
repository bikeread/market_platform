<template>
  <div class="app-container">
    <el-form :model="queryParams" class="main-search" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="会员号" prop="userNo">
        <el-input
          v-model="queryParams.userNo"
          placeholder="请输入会员号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input
          v-model="queryParams.mobile"
          placeholder="请输入会员手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="服务单号" prop="orderSn">
        <el-input
          v-model="queryParams.orderSn"
          placeholder="请输入服务单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属店铺" prop="storeIds">
        <el-select v-model="storeIds" multiple filterable clearable placeholder="请选择店铺" style="width: 100%;">
          <el-option
            v-for="item in storeList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择订单状态"
          clearable
        >
          <el-option v-for="statusItem in statusList" :key="statusItem.key+''" :label="statusItem.name" :value="statusItem.key+''"/>
        </el-select>
      </el-form-item>
      <el-form-item label="提交时间">
        <el-date-picker
          v-model="queryParams.startTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetime"
          style="width:190px"
          placeholder="开始时间"
        ></el-date-picker>
        <span class="sp"> ~ </span>
        <el-date-picker
          v-model="queryParams.endTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetime"
          style="width:190px"
          placeholder="结束时间"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table ref="tables" v-loading="loading" :data="list" @selection-change="handleSelectionChange" :default-sort="defaultSort" @sort-change="handleSortChange">
      <el-table-column label="服务单ID" prop="id" width="80"/>
      <el-table-column label="订单号"  align="center" width="190" prop="orderSn">
        <template slot-scope="scope">
          <span style="color: #00afff;cursor: pointer;">{{ scope.row.orderSn }}</span>
        </template>
      </el-table-column>
      <el-table-column label="会员号" width="130" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.userNo">
              <span>{{ scope.row.userNo }}</span>
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="会员名称" width="120" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.name">
              <span>{{ scope.row.name }}</span>
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="手机号称" width="120" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.mobile">
              <span>{{ scope.row.mobile }}</span>
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="车牌号" align="center" width="100" prop="VehiclePlateNo">
        <template slot-scope="scope">
          <span>{{ scope.row.vehiclePlateNo }}</span>
        </template>
      </el-table-column>
      <el-table-column label="所属店铺" align="center" width="140" prop="storeInfo.name">
        <template slot-scope="scope">
          <span v-if="scope.row.storeInfo">{{ scope.row.storeInfo.name }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80" prop="status">
        <template slot-scope="scope">
          <span class="status-normal">{{ getName(statusList, scope.row.status) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" width="150" prop="createTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="是否扫码" width="80" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row.scanCode">
              <span :title="scope.row.scanCode">是</span>
          </span>
          <span v-else>否</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" width="150" prop="updateTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="130" fixed='right'>
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
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

    <!-- 修改对话框 start-->
    <el-dialog :title="title" :visible.sync="open" class="common-dialog" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="服务单号" prop="orderSn">
              <el-input v-model="form.orderSn" placeholder="订单号" disabled maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注信息" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注信息"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="状态">
                <el-select v-model="form.status" placeholder="状态">
                  <el-option v-for="statusItem in statusList" :key="statusItem.key+''" :label="statusItem.name" :value="statusItem.key+''"/>
                </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 修改对话框 end-->
  </div>
</template>

<script>
import { getOrderList, getOrderInfo, saveOrder, deleteOrder } from "@/api/vehicleOrder";
export default {
  name: "VehicleOrder",
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
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 店铺列表
      storeList: [],
      // 所属店铺
      storeIds: [],
      // 订单类型
      typeList: [],
      // 订单状态列表
      statusList: [],
      // 是否显示弹出层
      open: false,
      // 默认排序
      defaultSort: {prop: 'createTime', order: 'descending'},
      storeInfo: {},
      orderInfo: {},
      // 表单参数
      form: { orderId: "", orderSn: '', remark: "", status: "A" },
      // 查询参数
      queryParams: {
        page: 1,
        pageSize: 10,
        userId: '',
        mobile: '',
        status: '',
        orderSn: '',
        storeIds: ''
      },
      // 表单校验
      rules: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 查询订单
    getList() {
      const app = this;
      app.loading = true;
      app.queryParams.storeIds = app.storeIds ? app.storeIds.join(",") : '';
      getOrderList(app.queryParams).then( response => {
          app.list = response.data.paginationResponse.content;
          app.total = response.data.paginationResponse.totalElements;
          app.storeList = response.data.storeList;
          app.statusList = response.data.statusList;
          app.loading = false;
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
      this.queryParams.status = '';
      this.queryParams.mobile = '';
      this.queryParams.orderSn = '';
      this.queryParams.storeIds = '';
      this.storeIds = [];
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
    // 表单重置
    reset() {
      this.form = {
        orderId: "",
        orderSn: "",
        status: "A",
        remark: ""
      };
      this.resetForm("form");
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 提交修改按钮
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
            saveOrder(this.form).then(response => {
               this.$modal.msgSuccess("修改成功！");
               this.open = false;
               this.getList();
            });
        }
      });
    },
    // 查看详情按钮操作
    handleView(row) {
      this.$router.push( { path: '/order/detail?orderId=' + row.id } )
    },
    // 修改按钮操作
    handleUpdate(row) {
      const app = this;
      app.reset();
      const id = row.id || this.ids;
      getOrderInfo(id).then(response => {
        let orderInfo = response.data;
        if (orderInfo) {
            app.form.orderId = orderInfo.id;
            app.form.orderSn = orderInfo.orderSn;
            app.form.remark = orderInfo.remark;
            app.form.status = orderInfo.status;
        }
        this.open = true;
        this.title = "修改服务单";
      });
    },
    // 删除按钮操作
    handleDelete(row) {
      const name = row.orderSn
      this.$modal.confirm('是否确认删除服务单号为"' + name + '"的数据项？').then(function() {
        return deleteOrder(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>

