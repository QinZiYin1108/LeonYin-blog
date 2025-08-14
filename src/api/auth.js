import http from '../utils/http'

export function login(payload) {
  return http.post('/auth/login', payload)
}

export function register(payload) {
  return http.post('/auth/register', payload)
}

export function sendCode(email) {
  return http.post('/auth/send-code?email=' + encodeURIComponent(email))
}

export function forgotPassword(payload) {
  return http.post('/auth/forgot-password', payload)
}





