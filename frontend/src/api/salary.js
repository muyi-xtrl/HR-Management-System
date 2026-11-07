import request from '@/utils/request'

// 获取我的薪资列表
export const getMySalaryList = (params) => {
  return request.get('/api/salary/list', { params })
}

// 获取全部薪资列表（HR/管理员）
export const getAllSalaryList = (params) => {
  return request.get('/api/salary/all', { params })
}

// 生成薪资
export const generateSalary = (data) => {
  return request.post('/api/salary/generate', data)
}

// 发放薪资
export const paySalary = (id) => {
  return request.put(`/api/salary/pay/${id}`)
}

// 获取薪资详情
export const getSalaryDetail = (id) => {
  return request.get(`/api/salary/detail/${id}`)
}