<template>
  <view class="page-container">
    <LuckyWheel @finish="onFinish" />
  </view>
</template>

<script>
import LuckyWheel from '@/components/activity/LuckyWheel.vue'

export default {
  components: { LuckyWheel },
  methods: {
    onFinish(prize) {
      console.log('抽奖结果:', prize)
      // 可在此处调用后端接口记录抽奖结果
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>
