<template>
  <!-- 添加或修改对话框 -->
  <el-dialog :title="title" :visible.sync="showDialog" class="common-dialog" width="840px" :before-close="handleCLose" @close="handleCLose" destroy-on-close>
    <el-form ref="form" :model="form" :rules="rules" label-width="120px">
      <el-row>
        <el-col :span="24">
          <el-form-item label="预约名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入预约名称" maxlength="200" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="所属店铺" prop="storeId">
            <el-select
              v-model="form.storeId"
              style="width: 260px"
              placeholder="所属店铺，空则为全部店铺"
            >
              <el-option :key="0" label="全部店铺" v-if="!this.$store.getters.storeId" :value="0"/>
              <el-option v-for="storeInfo in storeList" :key="storeInfo.id" :label="storeInfo.name" :value="storeInfo.id"/>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="所属分类" prop="cateId">
            <el-select
              v-model="form.cateId"
              style="width: 260px"
              placeholder="所属分类"
            >
              <el-option :key="0" label="无" :value="0"/>
              <el-option v-for="cate in cateList" :key="cate.id" :label="cate.name" :value="cate.id"/>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="9">
          <el-form-item label="封面图片" prop="logo">
            <el-upload
              :action="uploadAction"
              list-type="picture-card"
              :class="{hide:hideUpload}"
              :file-list="uploadFiles"
              :auto-upload="true"
              :show-file-list="false"
              :headers="uploadHeader"
              :on-success="handleUploadSuccess">
              <img
                v-if="this.form.logo"
                :src="imagePath + this.form.logo"
                class="list-img"
              />
              <i v-if="!this.form.logo" class="el-icon-plus"></i>
            </el-upload>
          </el-form-item>
        </el-col>
        <p class="form-tips">（提示：点击图片修改，建议尺寸：200 x 140）</p>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="可预约日期" prop="dates">
            <el-date-picker
              type="dates"
              v-model="dates"
              value-format="yyyy-MM-dd"
              style="width:500px"
              placeholder="选择一个或多个日期，空则表示未来7天每天都可预约"
            >
            </el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="可预约时段" prop="dates">
            <div class="add-item"><el-button type="danger" size="mini" icon="el-icon-plus" @click="addTime()">添加</el-button></div>
            <div class="time-item" v-for="(time, index) in times">
              <el-time-select
                placeholder="起始时间"
                v-model="time.startTime"
                :picker-options="{
                  start: '00:00',
                  step: '00:15',
                  end: '23:59'
                 }">
              </el-time-select>
              -
              <el-time-select
                placeholder="结束时间"
                v-model="time.endTime"
                :picker-options="{
                  start: '06:00',
                  step: '00:15',
                  end: '23:59',
                  minTime: time.startTime
                }">
              </el-time-select>
              -
              <el-input-number style="width: 120px" v-model="time.num" :min="0"/>
              <span class="remove-item el-icon-remove" @click="removeTime(index)"></span>
            </div>
            <div class="form-tips">提示：请填写起始时段、可预约上限人数</div>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="排序" prop="sort">
            <el-input-number v-model="form.sort" :min="0"/>
            <div class="form-tips">提示：数值越小，排列越靠前</div>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="备注信息">
            <el-input v-model="form.description" type="textarea" placeholder="请输入内容"></el-input>
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
      <el-button type="primary" @click="doSubmit">确定</el-button>
      <el-button @click="handleCLose">取消</el-button>
    </div>
  </el-dialog>
</template>
<script>
import { saveBook } from "@/api/book";
import { getToken } from '@/utils/auth';
export default {
  name: "bookForm",
  props: {
    showDialog: {
      type:[Boolean],
      default:()=>false
    },
    title: {
      type:[String],
      default:()=>''
    },
    form: {
      type:[Object],
      default:()=> null
    },
    imagePath: {
      type:[String],
      default:()=>''
    },
    storeList: {
      type:[Array],
      default:()=>[]
    },
    cateList: {
      type:[Array],
      default:()=>[]
    }
  },
  watch: {
    showDialog(value) {
      if (value) {
          if (this.form.serviceDates) {
              this.dates = this.form.serviceDates.split(",");
          } else {
              this.dates = [];
          }
          if (this.form.serviceTimes) {
             const times = this.form.serviceTimes.split(",");
             let timeArr = [];
             for (let i = 0; i < times.length; i ++) {
                  let am = times[i].split("-");
                  if (am && am.length == 3) {
                      let item = { startTime: am[0], endTime: am[1], num: am[2] };
                      timeArr.push(item);
                  }
             }
             if (timeArr.length > 0) {
                 this.times = timeArr;
             }
          } else if(this.form.id) {
              this.times = [];
          } else {
              this.times = [ { startTime: '08:30', endTime: '12:00', num: 1 }, { startTime: '14:00', endTime: '18:00', num: 1 } ];
          }
      }
    }
  },
  data(){
    return {
      loading: false,
      dates: [],
      times: [ { startTime: '08:30', endTime: '12:00', num: 1 }, { startTime: '14:00', endTime: '18:00', num: 1 } ],
      total: 0,
      // 上传地址
      uploadAction: process.env.VUE_APP_SERVER_URL + 'backendApi/file/upload',
      // 隐藏上传
      hideUpload: false,
      // 上传文件列表
      uploadFiles: [],
      uploadHeader: { 'Access-Token' : getToken() },
      rules: {
        name: [
          { required: true, message: "名称不能为空", trigger: "blur" },
          { min: 2, max: 200, message: '名称长度必须介于2 和 200 之间', trigger: 'blur' }
        ],
        logo: [{ required: true, message: "请上传图片", trigger: "blur" }]
      }
    }
  },
  methods: {
    // 提交
    doSubmit() {
      const app = this;
      let param = app.form;
      param.times = app.times;
      if (app.dates && app.dates.length > 0) {
          param.dates = app.dates.toString();
      } else {
          param.dates = '';
      }
      saveBook(param).then( response => {
        if (response) {
            app.$modal.msgSuccess("提交成功！");
            app.$emit('updateList', false);
            app.$emit('closeDialog', 'bookDialog');
        }
      })
    },
    // 关闭对话框
    handleCLose() {
      this.$emit('closeDialog', 'bookDialog');
    },
    // 上传成功回调
    handleUploadSuccess(file) {
      this.form.logo = file.data.fileName;
    },
    // 添加时间段
    addTime() {
      this.times.push( { startTime: '', endTime: '', num: 1 } );
    },
    removeTime(index) {
      const newTimes = [];
      this.times.forEach(function(item, i){
        if (index !== i) {
            newTimes.push(item);
        }
      });
      this.times = newTimes;
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
.time-item {
  margin-bottom: 2px;
}
.remove-item {
  margin-left: 2px;
  color: red;
  cursor: pointer;
}
</style>

