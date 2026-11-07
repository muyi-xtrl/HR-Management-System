import request from '@/utils/request'

export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data: data,  // ✅ 使用 data，不是 params
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

export const getCaptcha = () => {
  return request.get('/auth/captcha')
}