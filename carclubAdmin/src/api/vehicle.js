import request from '@/utils/request'

export function getVehicleList(query) {
  return request({
      url: 'backendApi/vehicle/list',
      method: 'get',
      params: query
  })
}

export function getVehicleInfo(vehicleId) {
    return request({
        url: 'backendApi/vehicle/info/' + vehicleId,
        method: 'get'
  })
}

export function saveVehicleInfo(data) {
  return request({
    url: 'backendApi/vehicle/save',
    method: 'post',
    data: data
  })
}

export function deleteVehicle(vehicleId) {
  return request({
    url: 'backendApi/vehicle/delete/' + vehicleId,
    method: 'get'
  })
}

export function updateVehicleStatus(vehicleId, status) {
  const data = {
    vehicleId,
    status
  }
  return request({
      url: 'backendApi/vehicle/updateStatus',
      method: 'post',
      data: data
  })
}
