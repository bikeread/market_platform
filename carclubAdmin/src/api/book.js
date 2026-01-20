import request from '@/utils/request'

// 分页预约分类列表
export function getBookCateList(param) {
  return request({
      url: 'backendApi/bookCate/list',
      method: 'get',
      params: param
  })
}

// 更新预约分类状态
export function updateBookCateStatus(cateId, status) {
  const data = {
    cateId,
    status
  }
  return request({
      url: 'backendApi/bookCate/updateStatus',
      method: 'post',
      data: data
  })
}

// 查询预约详情
export function getBookInfo(bookId) {
  return request({
    url: 'backendApi/book/info/' + bookId,
    method: 'get'
  })
}

// 保存预约分类
export function saveBookCate(data) {
  return request({
    url: 'backendApi/bookCate/save',
    method: 'post',
    data: data
  })
}

// 查询预约分类详情
export function getBookCateInfo(cateId) {
  return request({
    url: 'backendApi/bookCate/info/' + cateId,
    method: 'get'
  })
}

// 分页预约项目列表
export function getBookList(param) {
  return request({
    url: 'backendApi/book/list',
    method: 'get',
    params: param
  })
}

// 更新预约项目状态
export function updateBookStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: 'backendApi/book/updateStatus',
    method: 'post',
    data: data
  })
}

// 保存预约项目
export function saveBook(data) {
  return request({
    url: 'backendApi/book/save',
    method: 'post',
    data: data
  })
}

// 分页预约订单列表
export function getBookItemList(param) {
  return request({
    url: 'backendApi/bookItem/list',
    method: 'get',
    params: param
  })
}

// 更新预约订单状态
export function updateBookItemStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: 'backendApi/bookItem/updateStatus',
    method: 'post',
    data: data
  })
}
