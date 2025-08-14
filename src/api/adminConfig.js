import http from '../utils/http'

export function listConfigs() {
  return http.get('/admin/config')
}

export function getConfig(key) {
  return http.get(`/admin/config/${encodeURIComponent(key)}`)
}

export function deleteConfig(key) {
  return http.delete(`/admin/config/${encodeURIComponent(key)}`)
}

export function setAutoAvatar(value) {
  return http.post('/admin/config/auto-avatar?value=' + encodeURIComponent(value))
}

export function setDefaultAvatar(imageId) {
  return http.post('/admin/config/default-avatar?imageId=' + encodeURIComponent(imageId))
}

export function setDefaultCategoryIcon(imageId) {
  return http.post('/admin/config/default-category-icon?imageId=' + encodeURIComponent(imageId))
}






