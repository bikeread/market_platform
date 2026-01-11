import request from '@/utils/request'

// 分页查询订单列表
export function getOrderList(data) {
  return request({
      url: 'backendApi/vehicleOrder/list',
      method: 'get',
      params: data
  })
}

// 查询订单信息
export function getOrderInfo(orderId) {
  return request({
    url: 'backendApi/vehicleOrder/info/' + orderId,
    method: 'get'
  })
}

// 更新订单状态
export function updateOrderStatus(orderId, status) {
  const data = {
    orderId,
    status
  }
  return request({
      url: 'backendApi/vehicleOrder/updateStatus',
      method: 'post',
      data: data
  })
}

// 删除订单
export function deleteOrder(orderId) {
  return request({
    url: 'backendApi/vehicleOrder/delete/' + orderId,
    method: 'get'
  })
}

// 保存订单数据
export function saveOrder(data) {
  return request({
    url: 'backendApi/vehicleOrder/update',
    method: 'post',
    data: data
  })
}
