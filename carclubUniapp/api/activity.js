import request from '@/utils/request'

// API 地址配置
const api = {
  // 活动列表
  list: 'clientApi/activity/list',
  // 活动详情
  detail: 'clientApi/activity/detail',
  // 参与活动 (抽奖)
  participate: 'clientApi/activity/participate',
  // 抽奖记录
  records: 'clientApi/activity/records',
  // 兑换奖品
  exchange: 'clientApi/activity/exchange',
  // 获取用户剩余次数
  chances: 'clientApi/activity/chances',
  // 领取奖品
  claim: 'clientApi/activity/claim',
  // 集卡进度
  cardProgress: 'clientApi/activity/card/progress',
  // 抽取卡片
  drawCard: 'clientApi/activity/card/draw'
}

/**
 * 获取活动列表
 * @param {Object} param - 查询参数
 * @param {Object} option - 请求配置
 */
export const getActivityList = (param = {}, option = {}) => {
  const options = {
    isPrompt: true,
    load: true,
    ...option
  }
  return request.post(api.list, param, options)
}

/**
 * 获取活动详情
 * @param {Number} activityId - 活动ID
 */
export const getActivityDetail = (activityId) => {
  return request.post(api.detail, { activityId })
}

/**
 * 参与活动/抽奖
 * @param {Object} param - { activityId, activityType }
 */
export const participate = (param) => {
  return request.post(api.participate, param)
}

/**
 * 获取抽奖记录
 * @param {Object} param - 分页参数
 */
export const getRecords = (param = {}) => {
  return request.post(api.records, param)
}

/**
 * 获取用户剩余抽奖次数
 * @param {Number} activityId - 活动ID
 */
export const getChances = (activityId) => {
  return request.post(api.chances, { activityId })
}

/**
 * 领取/兑换奖品
 * @param {Object} param - { prizeId, userActivityId }
 */
export const claimPrize = (param) => {
  return request.post(api.claim, param)
}

/**
 * 获取集卡进度
 * @param {Number} activityId - 活动ID
 */
export const getCardProgress = (activityId) => {
  return request.post(api.cardProgress, { activityId })
}

/**
 * 抽取卡片
 * @param {Number} activityId - 活动ID
 */
export const drawCard = (activityId) => {
  return request.post(api.drawCard, { activityId })
}

// ============ 本地存储辅助函数 (前端实现阶段使用) ============

/**
 * 获取今日抽奖次数 (本地存储版本)
 * @param {String} activityType - 活动类型标识
 */
export const getLocalTodayCount = (activityType) => {
  const today = new Date().toDateString()
  const key = `activity_${activityType}_record`
  const saved = uni.getStorageSync(key) || {}
  if (saved.date === today) {
    return saved.count || 0
  }
  return 0
}

/**
 * 保存今日抽奖次数 (本地存储版本)
 * @param {String} activityType - 活动类型标识
 * @param {Number} count - 次数
 */
export const saveLocalTodayCount = (activityType, count) => {
  const today = new Date().toDateString()
  const key = `activity_${activityType}_record`
  uni.setStorageSync(key, {
    date: today,
    count: count
  })
}

/**
 * 获取集卡进度 (本地存储版本)
 * @param {String} activityType - 活动类型标识
 */
export const getLocalCardCollection = (activityType) => {
  const key = `activity_${activityType}_cards`
  return uni.getStorageSync(key) || {}
}

/**
 * 保存集卡进度 (本地存储版本)
 * @param {String} activityType - 活动类型标识
 * @param {Object} cards - 卡片数据
 */
export const saveLocalCardCollection = (activityType, cards) => {
  const key = `activity_${activityType}_cards`
  uni.setStorageSync(key, cards)
}
