# 饭电 Python 数据采集模块

本目录承载数据组负责的 Python 端能力：

- 静态或模拟网页数据采集：`crawler.py`
- 推荐分数计算：`recommender.py`
- Java 后端同步推送：`pusher.py`
- 命令行入口：`main.py`
- 本地 mock 后端：`mock_java_server.py`

## 环境初始化

```bash
cd crawler
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
```

Mac / Linux 激活命令：

```bash
source .venv/bin/activate
```

## 常用命令

采集静态数据并打印：

```bash
python main.py --crawl
```

解析模拟网页数据：

```bash
python main.py --crawl --source web
```

生成 Top-N 推荐：

```bash
python main.py --recommend --top-n 5 --prefer tasty
```

推送到后端同步接口：

```bash
python main.py --push --url http://<后端同学的局域网IP>:8080/api/admin/sync
```

启动本地 mock 后端测试推送：

```bash
python mock_java_server.py
python main.py --push
```

## 后端同步协议

默认同步接口：

```text
http://localhost:8080/api/admin/sync
```

默认鉴权请求头：

```json
{"Sync-Key": "your_sync_secret_key"}
```

可以用环境变量覆盖：

```bash
set FANDIAN_SYNC_KEY=fandian_2026_secret
```

默认请求体是后端当前 `SyncFoodRequest` 支持的 JSON 数组：

```json
[
  {
    "name": "二楼黄焖鸡",
    "price": 15.0,
    "canteen": "教一食堂",
    "window": "12号窗口"
  }
]
```

如果需要兼容旧接口的 `{"foods": [...]}` 包裹格式，可以加：

```bash
python main.py --push --wrap-payload
```
