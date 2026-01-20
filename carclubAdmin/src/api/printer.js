import request from '@/utils/request'

// 分页查询打印机列表
export function getPrinterList(query) {
  return request({
      url: 'backendApi/printer/list',
      method: 'get',
      params: query
  })
}

// 查询打印机信息
export function getPrinterInfo(printerId) {
  return request({
    url: 'backendApi/printer/info/' + printerId,
    method: 'get'
  })
}

// 更新打印机状态
export function updatePrinterStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
      url: 'backendApi/printer/updateStatus',
      method: 'post',
      data: data
  })
}

// 保存打印机
export function savePrinter(data) {
  return request({
    url: 'backendApi/printer/save',
    method: 'post',
    data: data
  })
}

// 保存打印设置
export function saveSetting(data) {
  return request({
    url: 'backendApi/printer/saveSetting',
    method: 'post',
    data: data
  })
}

// 获取打印配置
export function getSettingInfo() {
  return request({
    url: 'backendApi/printer/setting',
    method: 'get'
  })
}

// 打印订单信息
export function doPrint(orderId) {
  return request({
    url: 'backendApi/printer/doPrint/' + orderId,
    method: 'get'
  })
}
