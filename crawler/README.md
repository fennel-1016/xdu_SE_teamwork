饭电 (Fandian) - 自动化数据采集模块 🕷️
本模块目前为主项目的子文件夹，负责承载 Python 自动化数据爬取、清洗与数据批量同步脚本。
🛠️ 第一步：初始化 Python 虚拟环境
请数据组负责人按照以下步骤，在当前空文件夹内配置好独立的运行环境：
打开终端（Terminal），并切换到当前爬虫子目录：
cd crawler
创建 Python 虚拟环境（防止第三方库污染全局环境）：
python -m venv .venv
激活虚拟环境：
Windows 终端执行：.venv\Scripts\activate
Mac / Linux 终端执行：source .venv/bin/activate
（激活成功后，你的命令行开头会出现 (.venv) 提示字样）
安装网络请求与数据处理的基础依赖库：
pip install requests pandas
将当前环境的依赖导出为标准文件，方便团队其他成员同步：
pip freeze > requirements.txt
在当前目录下新建你的核心 Python 脚本文件，例如命名为 main.py 或 scraper.py。
🚀 第二步：数据推送协议与接口规范
爬虫脚本将数据抓取并清洗完毕后，需要通过 HTTP POST 请求，将数据定时批量推送至后端 Java 服务器。
目标推送 URL 地址：http://<后端同学的局域网IP>:8080/api/admin/sync
安全鉴权请求头 (Header)：{"Sync-Key": "fandian_2026_secret"}
请求体数据格式：标准的 JSON 数组（List[Dict]），每个字典内的字段必须严格对应以下命名规范：
[
{
"food_name": "二楼黄焖鸡",
"price": 15.00,
"canteen_name": "教一食堂",
"window_no": "12号窗口"
},
{
"food_name": "香辣牛肉面",
"price": 12.50,
"canteen_name": "教二食堂",
"window_no": "3号面档"
}
]
📌 当前核心功能开发节点（按优先级排序）
[ ] 1. 模拟数据（Mock）链路打通测试
不要急着写复杂的爬虫解析代码。优先在脚本中写死一段符合上述 JSON 格式的假数据，使用 Python 的 requests.post 带着 Sync-Key 请求头发送给后端同学。只要后端反馈 200 且 MySQL 数据库中成功出现了这几条记录，说明三端联调链路彻底打通。
[ ] 2. 编写真实美食抓取与清洗逻辑
编写针对目标校园社区、食堂公示平台或周边美食网站的抓取逻辑。在将数据组装成 JSON 之前，务必进行清洗：剔除包含乱码、缺失价格、缺失档口名称的脏数据，确保最终送入后端的数据池质量。
可自行更改调整readme文件