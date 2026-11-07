import request from '@/utils/request'

// 部门列表
export const getDepartmentList = (params) => {
  return request.get('/api/department/list', { params })
}

// 新增部门
export const addDepartment = (data) => {
  return request.post('/api/department/add', data)
}

// 修改部门
export const updateDepartment = (data) => {
  return request.put('/api/department/update', data)
}

// 删除部门
export const deleteDepartment = (id) => {
  return request.delete(`/api/department/delete/${id}`)
}

// 获取所有部门（下拉选择用）
export const getAllDepartments = () => {
  return request.get('/api/department/all')
}