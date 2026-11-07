const TOKEN_KEY = 'token'
const USER_KEY = 'user'

export const setToken = (token) => {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token)
  }
}

export const getToken = () => {
  return localStorage.getItem(TOKEN_KEY)
}

export const removeToken = () => {
  localStorage.removeItem(TOKEN_KEY)
}

// ✅ 修复：安全获取用户信息
export const setUser = (user) => {
  if (user) {
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  }
}

export const getUser = () => {
  try {
    const user = localStorage.getItem(USER_KEY)
    if (user && user !== 'undefined' && user !== 'null') {
      return JSON.parse(user)
    }
    return null
  } catch (error) {
    console.error('解析用户信息失败:', error)
    return null
  }
}

export const removeUser = () => {
  localStorage.removeItem(USER_KEY)
}

// 清除所有
export const clearAll = () => {
  removeToken()
  removeUser()
}