import http from '../utils/http'

export function pageUsers(payload) {
  return http.post('/admin/account/page', payload)
}

export function updateUser(userId, payload) {
  return http.put(`/admin/account/${encodeURIComponent(userId)}`, payload)
}

export function changeUserStatus(userId, payload) {
  return http.put(`/admin/account/${encodeURIComponent(userId)}/status`, payload)
}

export function resetUserPassword(userId, payload) {
  return http.post(`/admin/account/${encodeURIComponent(userId)}/reset-password`, payload)
}






