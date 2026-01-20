<template>
  <view class="container">
      <empty v-if="!storeInfo" :isLoading="isLoading" tips="数据加载中..."></empty>
      <block>
          <HomeBanner v-if="storeInfo" :banners="banner"/>
      </block>
      <block>
          <HomeUser v-if="storeInfo" :userInfo="userInfo" :vehicle="vehicle"/>
      </block>
      <block>
          <HomeNav v-if="storeInfo && navigation.length > 0" :navigation="navigation"/>
      </block>
      <!-- 活动中心入口 -->
      <block>
          <view class="activity-entry" @click="goToActivityCenter">
              <view class="activity-content">
                  <text class="activity-icon">🎡</text>
                  <view class="activity-info">
                      <text class="activity-title">营销活动中心</text>
                      <text class="activity-desc">15款互动抽奖游戏</text>
                  </view>
                  <text class="activity-arrow">›</text>
              </view>
          </view>
      </block>
      <block>
          <Goods v-if="storeInfo" :itemStyle="goodsStyle" :isReflash="isReflash" ref="mescrollItem" :params="goodsParams"/>
      </block>
  </view>
</template>

<script>
  import { setCartTabBadge, showMessage } from '@/utils/app'
  import Empty from '@/components/empty'
  import HomeBanner from "./components/HomeBanner.vue"
  import HomeUser from "./components/HomeUser.vue"
  import HomeNav from "./components/HomeNav.vue"
  import Goods from '@/components/page/goods'
  import * as settingApi from '@/api/setting'
  import * as Api from '@/api/page'
  import * as UserApi from '@/api/user'
  import MescrollCompMixin from "@/components/mescroll-uni/mixins/mescroll-comp.js";

  const App = getApp()
  
  export default {
    mixins: [MescrollCompMixin],
    components: {
       Empty,
       HomeBanner,
       HomeUser,
       HomeNav,
       Goods
    },
    data() {
      return {
        navigation: [],
        banner: [],
        storeInfo: null,
        userInfo: { id: '', avatar: '', name: '', balance: '', point: '' },
        vehicle: null,
        isReflash: false,
        isLoading: false,
        goodsStyle: {
            "background": "#F6F6F6",
            "display": "list",
            "column": 2,
            "show": ["goodsName", "goodsPrice", "linePrice", "sellingPoint", "goodsSales"]
        },
        goodsParams: {
            "source": "auto",
            "auto": {
                "category": 0,
                "goodsSort": "all",
                "showNum": 40
            }
        },
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad({ storeId }) {
      storeId = storeId ? parseInt(storeId) : 0;
      if (storeId > 0) {
          uni.setStorageSync('storeId', storeId);
          uni.setStorageSync("reflashHomeData", true);
      } else {
          this.getPageData();
      }
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
      const app = this;
      showMessage();
      setCartTabBadge();
      app.onGetStoreInfo();
      app.getUserInfo();
      uni.getLocation({
          type: 'gcj02',
          success(res){
              uni.setStorageSync('latitude', res.latitude);
              uni.setStorageSync('longitude', res.longitude);
              app.onGetStoreInfo();
          },
          fail(e) {
             // empty
          }
      })
    },

    methods: {

        /**
         * 跳转活动中心
         */
        goToActivityCenter() {
            uni.navigateTo({ url: '/activityPages/index/index' })
        },

        /**
         * 加载页面数据
         * @param {Object} callback
         */
        getPageData(callback) {
          const app = this;
          Api.home()
            .then(result => {
                 app.banner = result.data.banner;
                 app.navigation = result.data.navigation;
                 if (result.data.vehicle) {
                     app.vehicle = result.data.vehicle;
                 } else {
                     app.vehicle = null;
                 }
                 uni.removeStorageSync("reflashHomeData");
                 app.isReflash = false;
            })
            .finally(() => callback && callback())
        },
        
        /**
         * 获取用户信息
         * */
        getUserInfo() {
          const app = this;
          UserApi.info()
            .then(result => {
              app.userInfo = result.data.userInfo ? result.data.userInfo : { id: '', avatar: '', name: '', balance: '', point: '' };
            })
        },
        
        /**
         * 下拉刷新
         */
        onPullDownRefresh() {
          // 获取数据
          this.getPageData(() => {
             uni.stopPullDownRefresh()
          })
        },
        
        /**
         * 获取默认店铺
         * */
         onGetStoreInfo() {
            const app = this;
            settingApi.systemConfig()
             .then(result => {
                 app.storeInfo = result.data.storeInfo;
                 if (app.storeInfo) {
                     uni.setStorageSync("storeId", app.storeInfo.id);
                     uni.setStorageSync("merchantNo", app.storeInfo.merchantNo);
                     // 判断是否需要更新页面
                     let isReflash = uni.getStorageSync("reflashHomeData");
                     app.isReflash = isReflash;
                     if (isReflash === true) {
                         app.getPageData();
                     }
                 }
             })
         }
    },

    /**
     * 分享当前页面
     */
    onShareAppMessage() {
      const app = this
      return {
         title: "分享给你",
         path: "/pages/index/index?" + app.$getShareUrlParams()
      }
    },

    /**
     * 分享到朋友圈
     * 本接口为 Beta 版本，暂只在 Android 平台支持，详见分享到朋友圈 (Beta)
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/share-timeline.html
     */
    onShareTimeline() {
      const app = this
      const { page } = app
      return {
        title: page.params.share_title,
        path: "/pages/index/index?" + app.$getShareUrlParams()
      }
    }

  }
</script>

<style lang="scss" scoped>
.activity-entry {
    margin: 20rpx 10rpx;
    background: linear-gradient(135deg, #667eea, #764ba2);
    border-radius: 16rpx;
    box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.3);
    overflow: hidden;

    .activity-content {
        display: flex;
        align-items: center;
        padding: 30rpx;
    }

    .activity-icon {
        font-size: 64rpx;
        margin-right: 24rpx;
    }

    .activity-info {
        flex: 1;
        display: flex;
        flex-direction: column;

        .activity-title {
            font-size: 32rpx;
            font-weight: bold;
            color: #fff;
            margin-bottom: 8rpx;
        }

        .activity-desc {
            font-size: 24rpx;
            color: rgba(255, 255, 255, 0.8);
        }
    }

    .activity-arrow {
        font-size: 48rpx;
        color: rgba(255, 255, 255, 0.8);
    }
}
</style>
