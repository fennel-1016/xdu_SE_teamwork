"""饭电 Python 数据采集模块默认配置。"""

DEFAULT_WEIGHTS = {
    "w1": 0.5,  # 评分权重
    "w2": 0.2,  # 价格惩罚权重
    "w3": 0.3,  # 距离惩罚权重
}

PREFER_WEIGHTS = {
    "cheap": {"w1": 0.4, "w2": 0.4, "w3": 0.2},
    "near": {"w1": 0.4, "w2": 0.2, "w3": 0.4},
    "tasty": {"w1": 0.7, "w2": 0.15, "w3": 0.15},
}

DEFAULT_SYNC_URL = "http://localhost:8080/api/admin/sync"
DEFAULT_RECOMMEND_URL = "http://localhost:8080/api/recommend"
DEFAULT_SYNC_KEY = "fandian_2026_secret"

REQUEST_TIMEOUT = 5
MAX_RETRIES = 2
