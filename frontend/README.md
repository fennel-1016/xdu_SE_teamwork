# 饭电 Uni-app 前端

本目录是“饭电”微信小程序前端模块，基于 Uni-app + Vue2 实现课程项目 MVP。

## 已实现功能

- 启动后通过 `uni.login` 获取微信临时登录 `code`
- 调用后端 `POST /api/user/login`，缓存返回的 `openid`
- 首页展示随机转盘和“闪电抽选”入口
- 点击抽选后锁定按钮、播放转盘动画，并调用 `GET /api/food/random`
- 通过 `Authorization: OPENID` 请求头传递用户身份
- 展示菜品名称、价格、食堂和窗口信息
- 对登录失败、网络失败、空数据、身份失效等场景给出 Toast 提示

## 目录结构

```text
frontend/
├── App.vue
├── main.js
├── manifest.json
├── pages.json
├── pages/index/index.vue
└── utils/
    ├── api.js
    ├── auth.js
    ├── config.js
    └── request.js
```

## 本地联调

1. 用 HBuilderX 打开 `frontend` 目录。
2. 在 `manifest.json` 的微信小程序配置里填写团队测试 AppID。
3. 在 `utils/config.js` 中把 `API_BASE_URL` 改成后端地址，例如：

```js
export const API_BASE_URL = 'http://192.168.x.x:8080'
```

4. 运行到微信开发者工具。
5. 开发阶段需要在微信开发者工具中勾选“不校验合法域名、web-view、TLS 版本以及 HTTPS 证书”。

## 后端接口约定

登录：

```http
POST /api/user/login
Content-Type: application/json

{
  "code": "wx_login_code",
  "nickname": "饭电用户"
}
```

随机抽选：

```http
GET /api/food/random
Authorization: OPENID
```

响应统一按后端 `Result` 结构处理：

```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "name": "黄焖鸡",
    "price": 15.0,
    "canteen": "教一食堂",
    "window": "12号窗口"
  }
}
```
