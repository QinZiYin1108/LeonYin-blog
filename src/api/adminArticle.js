import http from '../utils/http'

export function pageArticles(payload) {
  return http.post('/article/page', payload)
}

export function getArticleDetail(articleId) {
  return http.get(`/article/${encodeURIComponent(articleId)}`)
}

export function createArticle(payload) {
  return http.post('/admin/article', payload)
}

export function updateArticle(articleId, payload) {
  return http.put(`/admin/article/${encodeURIComponent(articleId)}`, payload)
}

export function deleteArticle(articleId) {
  return http.delete(`/admin/article/${encodeURIComponent(articleId)}`)
}

export function toggleTop(articleId) {
  return http.put(`/admin/article/${encodeURIComponent(articleId)}/top`)
}


