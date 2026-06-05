# 饭电项目

饭电是一个面向校园食堂选择场景的微信小程序课程项目。项目包含 Uni-app 前端、Spring Boot 后端、Python 数据爬取模块，并提供 Docker 与 Kubernetes 部署配置。

## 项目结构

```text
xdu_SE_teamwork/
├── frontend/              # Uni-app 微信小程序前端
├── backend/               # Spring Boot 后端服务
├── crawler/               # Python 数据爬取与同步模块
├── docker/                # Docker 构建辅助配置
├── k8s/                   # Kubernetes 部署示例
├── _docs/                 # 需求、设计、测试等课程文档
├── Dockerfile             # 后端服务镜像构建文件
├── docker-compose.yml     # MySQL + 后端一键部署配置
└── DEPLOYMENT.md          # 部署说明
```

## 后端 Docker 部署

本项目已提供后端 Dockerfile 和 Docker Compose 配置。Docker Compose 会启动两个容器：

- `fandian-mysql`：MySQL 8.0 数据库
- `fandian-backend`：Spring Boot 后端服务

在项目根目录执行：

```bash
docker compose up -d --build
```

查看容器状态：

```bash
docker compose ps
```

访问后端接口：

```text
http://localhost:8080/api/food/random
```

未登录时返回 `{"code":401,"msg":"请先登录","data":null}` 属于正常结果，说明后端已经启动并进入鉴权流程。

停止服务：

```bash
docker compose down
```

更多部署细节见 [DEPLOYMENT.md](DEPLOYMENT.md)。

## 前端运行说明

前端位于 `frontend/` 目录，是 Uni-app 微信小程序项目。它不是普通 Web 前端，因此不会作为 Docker 容器启动；需要通过 HBuilderX 或微信开发者工具运行。

推荐操作流程：

1. 启动后端服务，确保 `http://localhost:8080` 可访问。
2. 打开 HBuilderX，选择 `文件 -> 打开目录`，打开 `frontend/`。
3. 检查 `frontend/manifest.json` 中的微信小程序 AppID，当前已配置为团队测试 AppID。
4. 检查 `frontend/utils/config.js` 中的 `API_BASE_URL`。
5. 如果使用手机真机或微信开发者工具访问本机后端，请把 `localhost` 改成电脑局域网 IP，例如 `http://192.168.x.x:8080`。
6. 在 HBuilderX 中选择 `运行 -> 运行到小程序模拟器 -> 微信开发者工具`。
7. 微信开发者工具中，开发阶段可勾选“不校验合法域名、web-view、TLS 版本以及 HTTPS 证书”。

前端详细说明见 [frontend/README.md](frontend/README.md)。

## 后端本地运行

如果不使用 Docker，也可以本地启动后端：

```bash
cd backend
mvn spring-boot:run
```

本地私密配置不提交到仓库，推荐放在：

```text
D:/00Tools/06weixinkey/application-local.yml
```

仓库中的 `backend/src/main/resources/application.yml` 会通过 `optional:file:` 自动导入该文件。

## 数据爬取模块

Python 数据爬取组代码位于 `crawler/`，用于采集食堂菜品并通过后端同步接口写入数据库。模块说明见 [crawler/README.md](crawler/README.md)。

## 课程文档

需求、设计、测试报告等材料位于 `_docs/` 目录。
