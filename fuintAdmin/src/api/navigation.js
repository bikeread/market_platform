import request from '@/utils/request'

// 查询导航栏
export function getNavigation() {
  return request({
    url: 'backendApi/navigate/info',
    method: 'get',
  })
}

// 保存导航栏
export function saveNavigation(data) {
  return request({
    url: 'backendApi/navigate/save',
    method: 'post',
    data: data,
  })
}
