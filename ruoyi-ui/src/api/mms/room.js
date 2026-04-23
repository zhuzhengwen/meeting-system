import request from '@/utils/request'

export function listRoom(query) {
  return request({ url: '/mms/room/list', method: 'get', params: query })
}

export function allRooms(query) {
  return request({ url: '/mms/room/all', method: 'get', params: query })
}

export function getRoom(roomId) {
  return request({ url: '/mms/room/' + roomId, method: 'get' })
}

export function getRoomMeetings(roomId) {
  return request({ url: '/mms/room/' + roomId + '/meetings', method: 'get' })
}

export function addRoom(data) {
  return request({ url: '/mms/room', method: 'post', data })
}

export function updateRoom(data) {
  return request({ url: '/mms/room', method: 'put', data })
}

export function delRoom(roomIds) {
  return request({ url: '/mms/room/' + roomIds, method: 'delete' })
}

export function submitInspection(data) {
  return request({ url: '/mms/room/inspect', method: 'post', data })
}
