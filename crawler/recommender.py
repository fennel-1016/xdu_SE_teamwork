"""推荐算法模块。"""

from copy import deepcopy
from typing import Dict, List, Optional

from config import DEFAULT_WEIGHTS, PREFER_WEIGHTS


def recommend(
    foods: List[Dict],
    top_n: int = 5,
    weights: Optional[Dict[str, float]] = None,
    prefer: Optional[str] = None,
    include_closed: bool = False,
) -> List[Dict]:
    """根据评分、价格、距离计算推荐分数，并返回 Top-N。"""
    if not foods:
        return []

    active_foods = [
        deepcopy(food)
        for food in foods
        if include_closed or food.get("available", True)
    ]

    if not active_foods:
        return []

    final_weights = _resolve_weights(weights, prefer)
    avg_price = _safe_average([float(food.get("price", 0)) for food in active_foods])
    max_distance = max(int(food.get("distance", 0)) for food in active_foods) or 1

    for food in active_foods:
        normalized_rating = float(food.get("rating", 0)) / 5.0
        normalized_price = float(food.get("price", 0)) / avg_price if avg_price else 1
        normalized_distance = int(food.get("distance", 0)) / max_distance

        score = (
            final_weights["w1"] * normalized_rating
            - final_weights["w2"] * normalized_price
            - final_weights["w3"] * normalized_distance
        )

        food["score"] = round(score, 4)

    active_foods.sort(key=lambda item: item["score"], reverse=True)
    return active_foods[:top_n]


def _resolve_weights(
    weights: Optional[Dict[str, float]],
    prefer: Optional[str],
) -> Dict[str, float]:
    """合并默认权重、偏好权重和用户自定义权重。"""
    if prefer:
        if prefer not in PREFER_WEIGHTS:
            raise ValueError("prefer 只支持 cheap、near、tasty")
        return PREFER_WEIGHTS[prefer].copy()

    if weights:
        merged = DEFAULT_WEIGHTS.copy()
        merged.update(weights)
        return merged

    return DEFAULT_WEIGHTS.copy()


def _safe_average(values: List[float]) -> float:
    """计算平均值，避免空列表和除零。"""
    valid_values = [value for value in values if value > 0]
    if not valid_values:
        return 1.0
    return sum(valid_values) / len(valid_values)
