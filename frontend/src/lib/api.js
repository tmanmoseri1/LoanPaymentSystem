import axios from 'axios'
import { getToken } from './auth.js'

/*const api = axios.create({
  baseURL: 'http://localhost:8081/api',
})*/

const api = axios.create({
  baseURL: '/api',
})

api.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default api
