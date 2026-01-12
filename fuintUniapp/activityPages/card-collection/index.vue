<template>
  <view class="page-container">
    <CardCollection @finish="onFinish" @draw="onDraw" />
  </view>
</template>

<script>
import CardCollection from '@/components/activity/CardCollection.vue'

export default {
  components: { CardCollection },
  methods: {
    onFinish(prize) {
      console.log('兑换奖品:', prize)
    },
    onDraw(card) {
      console.log('抽到卡片:', card)
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #56ab2f 0%, #a8e063 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>
