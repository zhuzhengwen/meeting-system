import request from '@/utils/request'

export function listTracking(query) {
  return request({ url: '/mms/tracking/list', method: 'get', params: query })
}

export function getTracking(trackingId) {
  return request({ url: '/mms/tracking/' + trackingId, method: 'get' })
}

export function addTracking(data) {
  return request({ url: '/mms/tracking', method: 'post', data })
}

export function updateTracking(data) {
  return request({ url: '/mms/tracking', method: 'put', data })
}

export function delTracking(trackingIds) {
  return request({ url: '/mms/tracking/' + trackingIds, method: 'delete' })
}

export function addProgress(data) {
  return request({ url: '/mms/tracking/progress', method: 'post', data })
}

export function getProgress(trackingId) {
  return request({ url: '/mms/tracking/progress/' + trackingId, method: 'get' })
}

export function closeTracking(trackingId) {
  return request({ url: '/mms/tracking/close/' + trackingId, method: 'put' })
}
