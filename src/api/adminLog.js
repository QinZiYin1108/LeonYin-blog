import http from '../utils/http'

export function pageUserLogs(params) {
	return http.get('/admin/log/page', { params })
}

export function getMetricsOverview() {
  return http.get('/admin/metrics/overview')
}

export function getVisitsTrend(params) {
  return http.get('/admin/metrics/visits-trend', { params })
}

export function getLatestUserLogs(params) {
  return http.get('/admin/metrics/latest-logs', { params })
}





