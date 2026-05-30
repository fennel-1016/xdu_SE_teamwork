# 饭电后端项目 - 文件结构与用途说明

## 项目概述
本项目是"饭电"微信小程序的 Java Spring Boot 后端服务，负责提供用户认证（微信登录）、
智能决策（随机抽选菜品）和后台数据同步（接收 Python 爬虫推送）三大核心功能。

## 文件结构及用途

```
饭电-backend/
│
├── pom.xml                          # Maven 项目配置，定义依赖（Spring Boot、JPA、MySQL 等）
├── .gitignore                       # Git 忽略规则
│
└── src/main/
    ├── java/com/fandian/
    │   │
    │   ├── FandianApplication.java          # 【启动类】Spring Boot 应用入口
    │   │
    │   ├── controller/                      # 【控制层】处理 HTTP 请求，参数校验，调用 Service
    │   │   ├── UserController.java          #   - POST /api/user/login    微信登录接口
    │   │   ├── FoodController.java          #   - GET  /api/food/random   随机抽选接口
    │   │   └── AdminController.java         #   - POST /api/admin/sync    数据同步接口（爬虫推送）
    │   │
    │   ├── service/                         # 【业务层】核心业务逻辑
    │   │   ├── UserService.java             #   - 登录注册：code换OpenID → 新用户入库 / 老用户更新
    │   │   ├── FoodService.java             #   - 随机抽选：ORDER BY RAND() 随机取菜
    │   │   │                                #   - 数据同步：批量 UPSERT 菜品
    │   │   └── WechatService.java           #   - 微信对接：调用 jscode2session 接口换取 OpenID
    │   │
    │   ├── model/
    │   │   ├── entity/                      # 【实体类】与数据库表一一对应（JPA 映射）
    │   │   │   ├── User.java                #   - t_user 表：openid, nickname, create_time, last_login_time
    │   │   │   └── Food.java                #   - t_food 表：id, food_name, price, canteen_name, window_no, sync_time
    │   │   └── dto/                         # 【数据传输对象】接口入参/出参的封装
    │   │       ├── Result.java              #   - 统一响应体：{ code, msg, data }
    │   │       ├── LoginRequest.java        #   - 登录请求：{ code, nickname }
    │   │       ├── LoginResponse.java       #   - 登录响应：{ openid }
    │   │       ├── FoodDTO.java             #   - 菜品返回：{ name, price, canteen, window }
    │   │       └── SyncFoodRequest.java     #   - 同步请求：{ name, price, canteen, window }
    │   │
    │   ├── repository/                      # 【数据访问层】Spring Data JPA 接口
    │   │   ├── UserRepository.java          #   - 用户表 CRUD
    │   │   └── FoodRepository.java          #   - 菜品表 CRUD + 随机查询 + UPSERT
    │   │
    │   └── config/                          # 【配置类】
    │       ├── GlobalExceptionHandler.java  #   - 全局异常拦截，将底层错误封装为友好 JSON
    │       ├── CorsConfig.java              #   - 跨域配置，允许小程序前端访问
    │       └── RestTemplateConfig.java      #   - HTTP 客户端配置（5秒超时）
    │
    └── resources/
        ├── application.yml                  # 【主配置】数据库连接、微信 appid/secret、同步密钥
        └── schema.sql                       # 【建表脚本】首次部署时手动执行，创建 fandian 库和表
```

## 快速启动步骤

### 1. 环境准备
- JDK 1.8+
- MySQL 8.0
- Maven 3.6+

### 2. 创建数据库
在 MySQL 中执行 `src/main/resources/schema.sql`：
```sql
source schema.sql;
```

### 3. 修改配置
编辑 `src/main/resources/application.yml`，修改以下配置项：
- `spring.datasource.password` — 你的 MySQL 密码
- `wechat.appid` — 你的微信小程序 AppID
- `wechat.secret` — 你的微信小程序 Secret
- `sync.secret-key` — 自定义的同步密钥（Python 爬虫也配相同的）

### 4. 启动项目
```bash
cd 饭电-backend
mvn clean package -DskipTests
java -jar target/fandian-backend-1.0.0.jar
```

或直接使用 Maven 插件：
```bash
mvn spring-boot:run
```

### 5. 验证接口
```bash
# 测试登录接口
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"code":"test_code", "nickname":"测试用户"}'

# 测试随机抽选接口
curl http://localhost:8080/api/food/random \
  -H "Authorization: USER_OPENID"

# 测试数据同步接口
curl -X POST http://localhost:8080/api/admin/sync \
  -H "Content-Type: application/json" \
  -H "Sync-Key: your_sync_secret_key" \
  -d '[{"name":"黄焖鸡","price":15.0,"canteen":"教一食堂","window":"12号"}]'
```

## 技术栈
| 组件 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.18 | 基础框架 |
| Spring Data JPA | — | ORM 数据访问 |
| MySQL | 8.0 | 关系型数据库 |
| RestTemplate | — | 调用微信 API |
| Lombok | — | 简化代码（可选） |
