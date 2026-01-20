import request from '@/utils/request'

// 生成二维码
export function createQrCode(data) {
  return request({
    url: 'backendApi/common/createQrCode',
    method: 'post',
    data: data
  })
}

