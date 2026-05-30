import { STORAGE_KEYS } from './config'
import { loginByCode } from './api'

function getStoredOpenid() {
  return uni.getStorageSync(STORAGE_KEYS.openid)
}

function setStoredOpenid(openid) {
  uni.setStorageSync(STORAGE_KEYS.openid, openid)
}

function getStoredNickname() {
  return uni.getStorageSync(STORAGE_KEYS.nickname) || '饭电用户'
}

function loginWithWeixinCode() {
  return new Promise((resolve, reject) => {
    uni.login({
      provider: 'weixin',
      success: resolve,
      fail: reject
    })
  })
}

export async function ensureLogin(force = false) {
  const cachedOpenid = getStoredOpenid()

  if (!force && cachedOpenid) {
    return cachedOpenid
  }

  const loginResult = await loginWithWeixinCode()

  if (!loginResult.code) {
    throw new Error('无法获取微信登录凭证，请稍后再试')
  }

  const response = await loginByCode(loginResult.code, getStoredNickname())
  const openid = response && response.openid

  if (!openid) {
    throw new Error('登录服务忙，请稍后再试')
  }

  setStoredOpenid(openid)
  return openid
}

export function clearLogin() {
  uni.removeStorageSync(STORAGE_KEYS.openid)
}
