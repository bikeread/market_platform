import request from '@/utils/request'

// 邀请记录列表
export function getCommissionRelationList(query) {
  return request({
    url: 'backendApi/commissionRelation/list',
    method: 'get',
    params: query,
  })
}

// 更新状态
export function updateCommissionRelation(id, status) {
  const data = {
    id,
    status,
  }
  return request({
    url: 'backendApi/commissionRelation/updateStatus',
    method: 'post',
    data: data,
  })
}

// 删除邀请记录
export function deleteCommissionRelation(id) {
  return request({
    url: 'backendApi/commissionRelation/delete/' + id,
    method: 'get',
  })
}
