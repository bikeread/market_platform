import request from '@/utils/request'

// 分页查询生成代码列表
export function getGenCodeList(query) {
  return request({
      url: 'backendApi/genCode/list',
      method: 'get',
      params: query
  })
}

// 查询生成代码信息
export function getGenCodeInfo(id) {
  return request({
    url: 'backendApi/genCode/info/' + id,
    method: 'get'
  })
}

// 更新生成代码状态
export function updateGenCodeStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
      url: 'backendApi/genCode/updateStatus',
      method: 'post',
      data: data
  })
}

// 保存生成代码
export function saveGenCode(data) {
  return request({
    url: 'backendApi/genCode/save',
    method: 'post',
    data: data
  })
}

// 执行生成代码
export function doGenCode(id) {
  return request({
    url: 'backendApi/genCode/gen/' + id,
    method: 'get'
  })
}
