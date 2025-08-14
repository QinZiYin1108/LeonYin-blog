import http from '../utils/http'

export function getArticleDetail(articleId) {
  return http.get(`/article/${articleId}`)
}

export function pageArticles(payload) {
  return http.post('/article/page', payload)
}

export function pageMyCollections(params) {
  return http.get('/user/collections/page', { params })
}



