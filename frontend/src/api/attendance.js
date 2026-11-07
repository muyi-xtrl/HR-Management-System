import request from '@/utils/request'

// 打卡上班
export const clockIn = () => {
  return request.post('/api/attendance/clockIn')
}

// 打卡下班
export const clockOut = () => {
  return request.post('/api/attendance/clockOut')
}

// 获取今日记录
export const getTodayRecord = () => {
  return request.get('/api/attendance/today')
}

// 获取考勤历史记录
export const getHistory = (params) => {
  return request.get('/api/attendance/list', { params })
}

// 获取考勤统计
export const getStatistics = (params) => {
  return request.get('/api/attendance/statistics', { params })
}

// 获取所有考勤记录（HR/管理员）
export const getAllAttendance = (params) => {
  return request.get('/api/attendance/all', { params })
}