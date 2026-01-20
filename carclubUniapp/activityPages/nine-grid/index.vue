<template>
  <view class="page-container">
    <NineGrid @finish="onFinish" />
  </view>
</template>

<script>
import NineGrid from '@/components/activity/NineGrid.vue'

export default {
  components: { NineGrid },
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
  background: linear-gradient(180deg, #ee0979 0%, #ff6a00 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>
