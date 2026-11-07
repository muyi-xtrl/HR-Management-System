import request from '@/utils/request'

// 分页查询员工
export const getEmployeeList = (params) => {
  return request.get('/api/employee/list', { params })
}

// 新增员工
export const addEmployee = (data) => {
  return request.post('/api/employee/add', data)
}

// 修改员工
export const updateEmployee = (data) => {
  return request.put('/api/employee/update', data)
}

// 删除员工
export const deleteEmployee = (id) => {
  return request.delete(`/api/employee/delete/${id}`)
}

// 导出 Excel
export const exportEmployee = (params) => {
  return request.get('/api/employee/export', { 
    params, 
    responseType: 'blob' 
  })
}

// 获取员工详情
export const getEmployeeDetail = (id) => {
  return request.get(`/api/employee/detail/${id}`)
}