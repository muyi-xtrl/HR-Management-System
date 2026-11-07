import request from '@/utils/request'

// 获取操作日志列表
export const getLogList = (params) => {
  return request.get('/api/log/list', { params })
}

// 导出操作日志
export const exportLog = (params) => {
  return request.get('/api/log/export', { 
    params, 
    responseType: 'blob' 
  })
}

// 清空日志（管理员）
export const clearLogs = () => {
  return request.delete('/api/log/clear')
}