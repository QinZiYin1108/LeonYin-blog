import http from '../utils/http'

export function listCategories() {
  return http.get('/admin/category/list')
}

export function createCategory(payload) {
  return http.post('/admin/category', payload)
}

export function updateCategory(categoryId, payload) {
  return http.put(`/admin/category/${encodeURIComponent(categoryId)}`, payload)
}

export function deleteCategory(categoryId) {
  return http.delete(`/admin/category/${encodeURIComponent(categoryId)}`)
}

export function toggleCategory(categoryId) {
  return http.put(`/admin/category/${encodeURIComponent(categoryId)}/toggle`)
}






