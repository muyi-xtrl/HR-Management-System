import request from '@/utils/request'

// 获取用户列表
export const getUserList = (params) => {
  return request.get('/api/user/list', { params })
}

// 新增用户
export const addUser = (data) => {
  return request.post('/api/user/add', data)
}

// 修改用户
export const updateUser = (data) => {
  return request.put('/api/user/update', data)
}

// 删除用户
export const deleteUser = (id) => {
  return request.delete(`/api/user/delete/${id}`)
}

// 重置密码
export const resetPassword = (id) => {
  return request.put(`/api/user/reset-password/${id}`)
}

// 获取用户详情
export const getUserDetail = (id) => {
  return request.get(`/api/user/detail/${id}`)
}