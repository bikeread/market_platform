<template>
    <div class="app-container">
      <el-form :model="queryParams" class="main-search" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="会员号" prop="userNo">
          <el-input
            v-model="queryParams.userNo"
            placeholder="请输入会员号"
            clearable
            style="width: 240px;"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input
            v-model="queryParams.mobile"
            placeholder="请输入会员手机号"
            clearable
            style="width: 240px;"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入会员名称"
            clearable
            style="width: 240px;"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="车牌号码" prop="plate">
          <el-input
            v-model="queryParams.plate"
            placeholder="请输入车牌号码"
            clearable
            style="width: 240px;"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="车辆类型" prop="vtype">
          <el-select
            v-model="queryParams.vtype"
            placeholder="车辆类型"
            clearable
            style="width: 240px"
          >
            <el-option
              v-for="item in carTypes"
              :key = "item.key"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="状态"
            clearable
            style="width: 240px"
          >
            <el-option key="A" label="启用" value="A"/>
            <el-option key="N" label="禁用" value="N"/>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          <el-button type="danger" icon="el-icon-plus" size="mini" @click="handleAdd">新增车辆</el-button>
        </el-form-item>
      </el-form>

      <el-table ref="tables" v-loading="loading" :data="list" @selection-change="handleSelectionChange" :default-sort="defaultSort" @sort-change="handleSortChange">
        <el-table-column label="车辆ID" prop="id" min-width="70"/>
        <el-table-column label="车牌号码" align="center" min-width="100" prop="vehiclePlateNo" />
        <el-table-column label="默认车辆" align="center" prop="isDefault">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.isDefault"
              active-value="Y"
              inactive-value="N"
              disabled
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="车辆品牌" align="center" prop="vehicleBrand">
          <template slot-scope="scope">
            <span>{{ scope.row.vehicleBrand ? scope.row.vehicleBrand : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="车辆型号" align="center" prop="vehicleModel">
          <template slot-scope="scope">
            <span>{{ scope.row.vehicleModel ? scope.row.vehicleModel : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="车辆类型" align="center" prop="vehicleType">
          <template slot-scope="scope">
            {{scope.row.vehicleType|vehicleTypeToString}}
          </template>
        </el-table-column>
        <el-table-column label="会员号" prop="userNo" width="150"/>
        <el-table-column label="会员名称" align="center" prop="name" />
        <el-table-column label="手机号" align="center" prop="mobile" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.mobile ? scope.row.mobile : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="添加时间" align="center" width="160" prop="createTime">
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
        <el-table-column label="操作" align="center" width="120" fixed='right'>
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              v-hasPermi="['vehicle:index']"
              @click="handleUpdate(scope.row)"
            >修改</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              v-hasPermi="['vehicle:index']"
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
     <el-dialog :title="title" :visible.sync="open" class="common-dialog" width="800px" append-to-body>
          <el-form ref="form" :model="form" :rules="rules" label-width="120px">
            <el-row>
              <el-col :span="24">
                <el-form-item label="车牌号码" prop="vehiclePlateNo">
                  <el-input v-model="form.vehiclePlateNo" placeholder="请输入车牌号码" maxlength="30"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="会员手机号" prop="mobile">
                  <el-input v-model="form.mobile" placeholder="请输入会员手机号" maxlength="30"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="会员名称" prop="name">
                  <el-input v-model="form.name" placeholder="请输入会员名称" maxlength="30"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="会员号" prop="userNo">
                  <el-input v-model="form.userNo" placeholder="请输入会员号，为空系统将自动分配" maxlength="30"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="车辆品牌" prop="vehicleBrand">
                  <el-input v-model="form.vehicleBrand" placeholder="请输入车辆品牌" maxlength="30"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="车辆型号" prop="vehicleModel">
                  <el-input v-model="form.vehicleModel" placeholder="请输入车辆型号" maxlength="30"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="车辆类型" prop="vehicleType">
                    <el-select
                      v-model="form.vehicleType"
                      placeholder="车辆类型"
                      style="width: 300px"
                    >
                      <el-option
                        v-for="item in carTypes"
                        :key = "item.key"
                        :label="item.label"
                        :value="item.value">
                      </el-option>
                    </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="车架号" prop="vin">
                  <el-input v-model="form.vin" placeholder="请输入车架号" maxlength="30"/>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </el-dialog>
    </div>
  </template>

  <script>
  import { getVehicleList, getVehicleInfo, saveVehicleInfo, deleteVehicle, updateVehicleStatus} from "@/api/vehicle";
  var that;
  export default {
    name: "VehicleIndex",
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
        // 是否显示修改对话框
        open: false,
        // 当前操作用户
        userId: '',
        // 是否弹层充值
        openBalance: false,
        // 是否弹层积分
        openPoint: false,
        // 日期范围
        dateRange: [],
        // 默认排序
        defaultSort: {prop: 'id', order: 'descending'},
        // 汽车类型
        carTypes: [{value: "C", label: "轿车"}, {value: "S", label: "SUV"}, {value: "M", label: "MPV"}],
        // 表单参数
        form: { id: '', name: '', mobile: '', userNo: '', status: "A", vehicleModel: '', vehicleBrand:'', vehiclePlateNo: '', vehicleColor:'', vehicleType: '', vin: '' },
        // 查询参数
        queryParams: {
          page: 1,
          pageSize: 10,
          mobile: '',
          id: '',
          userNo: '',
          name: '',
          plate: '',
          vtype: '',
          vin: '',
          status: ''
        },
        // 表单校验
        rules: {
          vehiclePlateNo: [
            { required: true, message: "车牌号不能为空", trigger: "blur" },
            { min: 2, max: 9, message: '车牌号格式错误', trigger: 'blur' }
          ]
        }
      };
    },
    filters: {
      vehicleTypeToString: function(type) {
        if (!type) {
            return '-';
        }
        return that.carTypes.find(function(t){return t.value === type}).label
      }
    },
    beforeCreate() {
      that = this;
    },
    created() {
      this.getList();
    },
    methods: {
      // 查询列表
      getList() {
        this.loading = true;
        getVehicleList(this.addDateRange(this.queryParams, this.dateRange)).then( response => {
            this.list = response.data.content;
            this.total = response.data.totalElements;
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
        this.$refs.tables.sort(this.defaultSort.prop, this.defaultSort.order);
        this.handleQuery();
      },
      // 状态修改
      handleStatusChange(row) {
        let text = row.status == "A" ? "启用" : "禁用";
        this.$modal.confirm('确认要' + text + '"' + row.vehiclePlateNo + '"吗？').then(function() {
          return updateVehicleStatus(row.id, row.status);
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
      handleSortChange(column, prop, order) {
        this.queryParams.orderByColumn = column.prop;
        this.queryParams.isAsc = column.order;
        this.getList();
      },
      // 余额充值操作
      handleBalance(userId) {
         this.openBalance = true;
         this.userId = userId.toString();
      },
      // 积分变更操作
      handlePoint(userId) {
         this.openPoint = true
         this.userId = userId.toString();
      },
      // 关闭对话框
      closeDialog() {
        this.open = false;
        this.userId = "";
        this.getList();
      },
      // 新增按钮操作
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "新增车辆";
      },
      // 表单重置
      reset() {
        this.resetForm("form");
        this.form.id = '';
        this.form.mobile = '';
        this.form.name = '';
        this.form.userNo = '';
        this.form.mobile = '';
        this.form.vin = '';
        this.form.vehiclePlateNo = '';
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
                saveVehicleInfo(this.form).then(response => {
                  this.$modal.msgSuccess("修改车辆成功");
                  this.open = false;
                  this.getList();
                });
            } else {
                saveVehicleInfo(this.form).then(response => {
                  this.$modal.msgSuccess("新增车辆成功");
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
        getVehicleInfo(id).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "编辑车辆";
        });
      },
      // 删除按钮操作
      handleDelete(row) {
        this.$modal.confirm('确定删除"' +  row.vehiclePlateNo + '"信息吗？').then(function() {
          return deleteVehicle(row.id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
      }
    }
  };
  </script>

