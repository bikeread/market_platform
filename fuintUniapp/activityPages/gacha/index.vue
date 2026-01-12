<template>
  <view class="page-container">
    <GachaMachine @finish="onFinish" />
  </view>
</template>

<script>
import GachaMachine from '@/components/activity/GachaMachine.vue'

export default {
  components: { GachaMachine },
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
  background: linear-gradient(180deg, #a8edea 0%, #fed6e3 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>
