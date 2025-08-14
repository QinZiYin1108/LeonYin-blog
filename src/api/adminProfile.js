import http from '../utils/http'

export function listAllProfiles() {
  return http.get('/admin/profile')
}

export function updateUserProfileByAdmin(userId, payload) {
  return http.put(`/admin/profile/${encodeURIComponent(userId)}`, payload)
}

export function resetAvatarToDefault(userId) {
  return http.put(`/admin/profile/${encodeURIComponent(userId)}/reset-avatar`)
}


