<template>
  <view class="page-container">
    <ShakeIt @finish="onFinish" />
  </view>
</template>

<script>
import ShakeIt from '@/components/activity/ShakeIt.vue'

export default {
  components: { ShakeIt },
  methods: {
    onFinish(prize) {
      console.log('抽奖结果:', prize)
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #00c6ff 0%, #0072ff 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>
