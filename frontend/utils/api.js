import { request } from './request'

export function loginByCode(code, nickname) {
  return request({
    url: '/api/user/login',
    method: 'POST',
    data: {
      code,
      nickname: nickname || '饭电用户'
    }
  })
}

export function getRandomFood(openid) {
  return request({
    url: '/api/food/random',
    method: 'GET',
    header: {
      Authorization: openid
    }
  })
}
