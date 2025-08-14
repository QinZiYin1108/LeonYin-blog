import http from '../utils/http'

export function getProfile() {
  return http.get('/user/profile')
}

export function updateProfile(payload) {
  return http.put('/user/profile', payload)
}

export function updateAvatar(imageId) {
  return http.put('/user/avatar?imageId=' + encodeURIComponent(imageId))
}

export function changePassword(payload) {
  return http.put('/user/password', payload)
}

export function deleteAccount() {
  return http.delete('/user/account')
}


