"""Java 后端同步模块。"""

import logging
import time
from typing import Dict, List, Optional

import requests

from config import DEFAULT_SYNC_KEY, DEFAULT_SYNC_URL, MAX_RETRIES, REQUEST_TIMEOUT


logger = logging.getLogger(__name__)


def build_sync_payload(foods: List[Dict]) -> List[Dict]:
    """转换为后端同步协议要求的 JSON 数组字段。"""
    payload = []

    for food in foods:
        payload.append(
            {
                "food_name": food.get("food_name") or food.get("name"),
                "price": food.get("price"),
                "canteen_name": (
                    food.get("canteen_name") or food.get("category") or "未知食堂"
                ),
                "window_no": food.get("window_no") or "未知窗口",
            }
        )

    return payload


def push_to_java(
    foods: List[Dict],
    server_url: Optional[str] = None,
    sync_key: str = DEFAULT_SYNC_KEY,
    wrap_payload: bool = False,
) -> bool:
    """
    将美食数据通过 HTTP POST 推送到 Java Spring Boot 后端。

    Args:
        foods: 美食条目列表。
        server_url: 后端接口地址，默认使用 config.DEFAULT_SYNC_URL。
        sync_key: 后端约定的 Sync-Key 请求头。
        wrap_payload: 是否使用 {"foods": [...]} 包裹数据，默认按项目协议直接发送数组。

    Returns:
        推送成功返回 True，否则返回 False。
    """
    url = server_url or DEFAULT_SYNC_URL
    sync_payload = build_sync_payload(foods)
    payload = {"foods": sync_payload} if wrap_payload else sync_payload
    headers = {"Content-Type": "application/json", "Sync-Key": sync_key}

    for attempt in range(1, MAX_RETRIES + 2):
        try:
            logger.info("正在推送数据到 Java 后端：%s，第 %s 次尝试", url, attempt)
            response = requests.post(
                url,
                json=payload,
                headers=headers,
                timeout=REQUEST_TIMEOUT,
            )
            response.raise_for_status()

            logger.info("推送成功，状态码：%s", response.status_code)
            return True

        except requests.RequestException as exc:
            logger.error("推送失败：%s", exc)

            if attempt <= MAX_RETRIES:
                time.sleep(1)
            else:
                logger.error("已达到最大重试次数，停止推送。")

    return False
