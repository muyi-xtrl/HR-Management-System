import request from '@/utils/request'

// 获取当前用户菜单
export const getMenus = () => {
  return request.get('/api/menu/current')
}

// 获取所有菜单（管理员用）
export const getAllMenus = () => {
  return request.get('/api/menu/all')
}

// 新增菜单
export const addMenu = (data) => {
  return request.post('/api/menu/add', data)
}

// 修改菜单
export const updateMenu = (data) => {
  return request.put('/api/menu/update', data)
}

// 删除菜单
export const deleteMenu = (id) => {
  return request.delete(`/api/menu/delete/${id}`)
}