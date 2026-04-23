import request from '@/utils/request'

export function listMeeting(query) {
  return request({ url: '/mms/meeting/list', method: 'get', params: query })
}

export function getMeetingSchedule(date) {
  return request({ url: '/mms/meeting/schedule', method: 'get', params: { date } })
}

export function getMeeting(meetingId) {
  return request({ url: '/mms/meeting/' + meetingId, method: 'get' })
}

export function addMeeting(data) {
  return request({ url: '/mms/meeting', method: 'post', data })
}

export function updateMeeting(data) {
  return request({ url: '/mms/meeting', method: 'put', data })
}

export function delMeeting(meetingIds) {
  return request({ url: '/mms/meeting/' + meetingIds, method: 'delete' })
}

export function cancelMeeting(meetingId) {
  return request({ url: '/mms/meeting/cancel/' + meetingId, method: 'put' })
}

export function completeMeeting(meetingId) {
  return request({ url: '/mms/meeting/complete/' + meetingId, method: 'put' })
}

export function delegateAttendee(data) {
  return request({ url: '/mms/meeting/delegate', method: 'put', data })
}
