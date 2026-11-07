import request from '@/utils/request'

// 申请请假
export const applyLeave = (data) => {
  return request.post('/api/leave/apply', data)
}

// 获取我的请假列表
export const getMyLeaveList = (params) => {
  return request.get('/api/leave/list', { params })
}

// 获取待审批列表
export const getApproveList = () => {
  return request.get('/api/leave/approve/list')
}

// 审批请假（通过/驳回）
export const approveLeave = (data) => {
  return request.put('/api/leave/approve', data)
}

// 获取请假详情
export const getLeaveDetail = (id) => {
  return request.get(`/api/leave/detail/${id}`)
}