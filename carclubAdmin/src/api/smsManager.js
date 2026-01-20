import request from '@/utils/request'

// 分页查询短信列表
export function getSmsList(query) {
  return request({
      url: 'backendApi/smsManager/list',
      method: 'get',
      params: query
  })
}

// 发送短信
export function sendSms(data) {
  return request({
    url: 'backendApi/smsManager/send',
    method: 'post',
    data: data
  })
}

// 查询短信设置
export function getSmsSetting() {
  return request({
    url: 'backendApi/smsManager/setting',
    method: 'get'
  })
}

// 保存短信设置
export function saveSetting(data) {
  return request({
    url: 'backendApi/smsManager/saveSetting',
    method: 'post',
    data: data
  })
}
