import http from '../utils/http'

export function pageUserLogs(params) {
	return http.get('/admin/log/page', { params })
}





