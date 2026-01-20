import request from '@/utils/request'

// 查询分佣记录列表
export function getCommissionLogList(query) {
  return request({
    url: 'backendApi/commissionLog/list',
    method: 'get',
    params: query,
  })
}

// 查询分佣记录详情
export function getCommissionLogDetail(query) {
  return request({
    url: 'backendApi/commissionLog/detail',
    method: 'get',
    params: query,
  })
}

// 更新分佣记录
export function updateCommissionLog(data) {
  return request({
    url: 'backendApi/commissionLog/save',
    method: 'post',
    data: data,
  })
}

// 作废分佣记录
export function deleteCommissionLog(id) {
  return request({
    url: 'backendApi/commissionLog/delete/' + id,
    method: 'get',
  })
}

// 结算分佣记录
export function doSettle(data) {
  return request({
    url: 'backendApi/commissionLog/doSettle',
    method: 'post',
    data: data,
  })
}
