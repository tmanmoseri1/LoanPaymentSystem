const TOKEN_KEY = 'token'

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function logout() {
  clearToken()
}

export function isAuthenticated() {
  return !!getToken()
}

export function isAuthed() {
  return isAuthenticated()
}