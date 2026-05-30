"""数据采集模块：支持静态数据和模拟网页解析。"""

from copy import deepcopy
from typing import Dict, List


STATIC_FOODS = [
    {"id": 1, "name": "一食堂香辣鸡腿饭", "price": 16.0, "rating": 4.7, "distance": 180, "category": "食堂窗口", "available": True},
    {"id": 2, "name": "二食堂番茄牛腩面", "price": 18.5, "rating": 4.6, "distance": 260, "category": "食堂窗口", "available": True},
    {"id": 3, "name": "校门口黄焖鸡米饭", "price": 22.0, "rating": 4.5, "distance": 620, "category": "校外美食", "available": True},
    {"id": 4, "name": "清真窗口牛肉拉面", "price": 15.0, "rating": 4.4, "distance": 210, "category": "食堂窗口", "available": True},
    {"id": 5, "name": "三食堂麻辣香锅", "price": 28.0, "rating": 4.8, "distance": 420, "category": "食堂窗口", "available": True},
    {"id": 6, "name": "后街烤冷面", "price": 9.0, "rating": 4.2, "distance": 780, "category": "校外小吃", "available": True},
    {"id": 7, "name": "图书馆咖啡轻食", "price": 24.0, "rating": 4.3, "distance": 120, "category": "校园轻食", "available": True},
    {"id": 8, "name": "东门炸鸡汉堡", "price": 26.0, "rating": 4.1, "distance": 900, "category": "校外美食", "available": False},
    {"id": 9, "name": "水果捞酸奶杯", "price": 13.5, "rating": 4.0, "distance": 300, "category": "校园甜品", "available": True},
    {"id": 10, "name": "北区砂锅米线", "price": 17.0, "rating": 4.6, "distance": 510, "category": "食堂窗口", "available": True},
]


MOCK_HTML = """
<html>
<body>
  <div class="food" data-id="1" data-available="true">
    <h3>模拟网页鸡排饭</h3>
    <span class="price">19.0</span>
    <span class="rating">4.5</span>
    <span class="distance">350</span>
    <span class="category">模拟网页</span>
  </div>
  <div class="food" data-id="2" data-available="true">
    <h3>模拟网页牛肉粉</h3>
    <span class="price">16.5</span>
    <span class="rating">4.3</span>
    <span class="distance">280</span>
    <span class="category">模拟网页</span>
  </div>
</body>
</html>
"""


def fetch_food_data(source: str = "static") -> List[Dict]:
    """获取美食数据。"""
    if source == "static":
        return deepcopy(STATIC_FOODS)

    if source == "web":
        return _parse_mock_html(MOCK_HTML)

    raise ValueError("source 只支持 'static' 或 'web'")


def _parse_mock_html(html: str) -> List[Dict]:
    """解析模拟 HTML 页面，展示爬虫解析流程。"""
    try:
        from bs4 import BeautifulSoup
    except ImportError as exc:
        raise RuntimeError("请先安装 beautifulsoup4：pip install beautifulsoup4") from exc

    soup = BeautifulSoup(html, "html.parser")
    foods = []

    for item in soup.select(".food"):
        foods.append(
            {
                "id": int(item.get("data-id")),
                "name": item.select_one("h3").get_text(strip=True),
                "price": float(item.select_one(".price").get_text(strip=True)),
                "rating": float(item.select_one(".rating").get_text(strip=True)),
                "distance": int(item.select_one(".distance").get_text(strip=True)),
                "category": item.select_one(".category").get_text(strip=True),
                "available": item.get("data-available") == "true",
            }
        )

    return foods
