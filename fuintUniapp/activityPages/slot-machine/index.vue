<template>
  <view class="page-container">
    <SlotMachine @finish="onFinish" />
  </view>
</template>

<script>
import SlotMachine from '@/components/activity/SlotMachine.vue'

export default {
  components: { SlotMachine },
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
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>
