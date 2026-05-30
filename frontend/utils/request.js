import { API_BASE_URL } from './config'

export function request(options) {
  const header = {
    'Content-Type': 'application/json',
    ...(options.header || {})
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: `${API_BASE_URL}${options.url}`,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      timeout: options.timeout || 5000,
      success(response) {
        const body = response.data || {}

        if (response.statusCode < 200 || response.statusCode >= 300) {
          reject(new Error(body.msg || '食堂断网了，请稍后再试'))
          return
        }

        if (body.code && body.code !== 200) {
          reject(new Error(body.msg || '操作失败，请重试'))
          return
        }

        resolve(body.data)
      },
      fail() {
        reject(new Error('信号被饭香挡住了，请重试'))
      }
    })
  })
}
