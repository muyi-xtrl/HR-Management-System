import request from '@/utils/request'

// 获取所有角色
export const getRoleList = () => {
  return request.get('/api/role/list')
}

// 获取角色菜单权限
export const getRoleMenus = (roleId) => {
  return request.get(`/api/role/menus/${roleId}`)
}

// 分配角色菜单
export const assignRoleMenus = (data) => {
  return request.put('/api/role/menus', data)
}