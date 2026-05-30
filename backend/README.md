饭电 (Fandian) - 后端业务服务层 ⚙️
本模块目前为主项目的子文件夹，负责承载基于 Spring Boot 框架构建的业务服务端。
🛠️ 第一步：初始化 Spring Boot 项目
请后端负责人按照以下步骤，把框架结构初始化到当前 backend 文件夹内：
打开 IntelliJ IDEA，点击 New Project。
选择 Spring Initializr（如果没有，请检查是否安装了相关插件或使用官方脚手架网站 start.spring.io 生成后导入）。
项目基本信息配置：
Language: Java (推荐使用 JDK 1.8 或 17)
Type: Maven
Packaging: Jar
在依赖选择 (Dependencies) 界面，务必勾选以下基础组件：
Spring Web (用于构建 RESTful API 接口)
MySQL Driver (MySQL 数据库驱动)
Lombok (用于简化 Entity/DTO 的 getter/setter 编写)
持久层框架（根据团队习惯自行二选一：MyBatis Framework / MyBatis-Plus，或者 Spring Data JPA）
项目生成路径：选择你们本地的 XDU_SE_TEAMWORK/backend 目录，直接覆盖该空文件夹。
🚀 第二步：数据库与本地环境搭建
在本地 MySQL 中建立名为 fandian_db 的数据库，编码格式务必设置为 utf8mb4（防止后续微信昵称带表情包导致报错）。
在 backend 目录下新建一个 sql 文件夹，将团队共用的建表语句存入 init.sql，并运行它初始化 t_user 和 t_food 表。
修改 application.properties 或 application.yml 配置文件，将数据库连接的 url、username 和 password 改为你本地的配置。
每日联调配合：本地服务默认启动在 8080 端口。启动成功后，在终端运行 ipconfig (Windows) 或 ifconfig (Mac/Linux)，找到你的局域网 IPv4 地址（如 192.168.1.100），将其发在项目微信群里，供前端组和爬虫组绑定调试。
📌 当前核心功能开发节点（按优先级排序）
[ ] 1. 用户认证接口 (POST /api/user/login)
接收前端传来的微信 code，在后端利用 RestTemplate 或 HttpClient 向微信官方授权服务器发起请求，换取用户的唯一标识 OpenID。随后在 t_user 表中检索，若为新用户则自动注册，老用户则更新最后登录时间，最终将 OpenID 返回给前端。
[ ] 2. 随机决策接口 (GET /api/food/random)
响应小程序的抽选请求。从 Header 中校验 Authorization 带来的用户 OpenID。随后连接数据库，利用高效的随机 SQL 语句（如 SELECT * FROM t_food ORDER BY RAND() LIMIT 1）提取一条美食数据，并以标准的 JSON 格式返回给前端。
[ ] 3. 数据同步接口 (POST /api/admin/sync)
开放给爬虫组的批量数据推送接口。为了安全，必须在 Header 中校验双方约定好的私密密钥（Sync-Key）。校验通过后，接收解包后的美食 JSON 数组，执行批量插入或更新（建议利用 MySQL 的联合唯一索引配合 INSERT INTO ... ON DUPLICATE KEY UPDATE，防止重复数据流入）。
可自行更改调整readme文件