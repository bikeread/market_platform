import request from '@/utils/request'

// 分页查询提现列表
export function getCashList(query) {
  return request({
    url: 'backendApi/commissionCash/list',
    method: 'get',
    params: query,
  })
}

// 查询提现详情
export function getCashDetail(bannerId) {
  return request({
    url: 'backendApi/commissionCash/info/' + bannerId,
    method: 'get',
  })
}

// 付款到会员余额
export function payToBalance(data) {
  return request({
    url: 'backendApi/commissionCash/payToBalance',
    method: 'post',
    data: data,
  })
}

// 更新提现
export function updateCommissionCash(data) {
  return request({
    url: 'backendApi/commissionCash/save',
    method: 'post',
    data: data,
  })
}

// 确认结算
export function confirmCommissionCash(data) {
  return request({
    url: 'backendApi/commissionCash/confirm',
    method: 'post',
    data: data,
  })
}

// 取消结算
export function cancelCommissionCash(data) {
  return request({
    url: 'backendApi/commissionCash/cancel',
    method: 'post',
    data: data,
  })
}
