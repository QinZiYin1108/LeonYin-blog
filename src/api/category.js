import http from '../utils/http'

export function listEnabledCategories() {
  return http.get('/category/enabled')
}

export function getCategoryById(categoryId) {
  return http.get(`/category/${categoryId}`)
}





