# 饭电项目部署说明

本项目后端是 Spring Boot 服务，部署交付物包含：

- `Dockerfile`：构建饭电后端镜像
- `docker-compose.yml`：本地一键启动 MySQL + 后端

## 1. Docker 镜像构建

在项目根目录执行：

```bash
docker build -t fandian-backend:1.0.0 .
```

运行容器：

```bash
docker run --name fandian-backend -p 8080:8080 --env-file backend/.env.example fandian-backend:1.0.0
```

## 2. Docker Compose 本地部署

在项目根目录执行：

```bash
docker compose up -d --build
```

查看日志：

```bash
docker compose logs -f backend
```

访问接口：

```text
http://localhost:8080/api/food/random
```

未登录访问时返回 `401 请先登录` 属于正常结果，说明服务已启动并且鉴权生效。

停止服务：

```bash
docker compose down
```

如果需要同时清理 MySQL 数据卷：

```bash
docker compose down -v
```

## 3. 配置说明

容器运行时通过环境变量覆盖后端配置：

| 环境变量 | 说明 |
| --- | --- |
| `SPRING_DATASOURCE_URL` | MySQL 连接地址 |
| `SPRING_DATASOURCE_USERNAME` | MySQL 用户名 |
| `SPRING_DATASOURCE_PASSWORD` | MySQL 密码 |
| `WECHAT_APPID` | 微信小程序 AppID |
| `WECHAT_SECRET` | 微信小程序密钥 |
| `SYNC_SECRET_KEY` | Python 爬虫同步密钥 |

注意：真实 `WECHAT_SECRET` 不建议提交到公开仓库，提交作业时可保留占位符。
