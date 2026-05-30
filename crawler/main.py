"""饭电 Python 数据采集、推荐与同步主程序。"""

import argparse
import json
import logging
from pathlib import Path
from typing import Dict, List, Optional

from config import DEFAULT_RECOMMEND_URL, DEFAULT_SYNC_URL
from crawler import fetch_food_data
from pusher import push_to_java
from recommender import recommend


logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
)

logger = logging.getLogger(__name__)


def load_foods_from_file(file_path: str) -> List[Dict]:
    """从 JSON 文件加载美食数据。"""
    path = Path(file_path)

    if not path.exists():
        raise FileNotFoundError(f"文件不存在：{file_path}")

    with path.open("r", encoding="utf-8") as file:
        return json.load(file)


def save_json(data, file_path: str) -> None:
    """保存 JSON 数据到文件。"""
    path = Path(file_path)

    with path.open("w", encoding="utf-8") as file:
        json.dump(data, file, ensure_ascii=False, indent=2)

    logger.info("数据已保存到：%s", file_path)


def print_json(data) -> None:
    """格式化输出 JSON 到 stdout。"""
    print(json.dumps(data, ensure_ascii=False, indent=2))


def build_parser() -> argparse.ArgumentParser:
    """构建命令行参数解析器。"""
    parser = argparse.ArgumentParser(description="饭电 Python 数据采集、推荐与同步工具")

    parser.add_argument("--crawl", action="store_true", help="运行数据采集")
    parser.add_argument("--recommend", action="store_true", help="运行推荐算法")
    parser.add_argument("--push", action="store_true", help="推送数据到 Java 后端")

    parser.add_argument("--source", default="static", choices=["static", "web"], help="采集数据源")
    parser.add_argument("--input", help="从 JSON 文件读取数据")
    parser.add_argument("--output", help="将采集或推荐结果输出到 JSON 文件")

    parser.add_argument("--top-n", type=int, default=5, help="推荐数量")
    parser.add_argument("--prefer", choices=["cheap", "near", "tasty"], help="推荐偏好")
    parser.add_argument("--include-closed", action="store_true", help="推荐时包含未营业条目")

    parser.add_argument(
        "--url",
        default=None,
        help="推送接口地址；默认同步接口为 localhost:8080/api/admin/sync",
    )
    parser.add_argument(
        "--wrap-payload",
        action="store_true",
        help='推送时使用 {"foods": [...]} 包裹数据，便于兼容旧接口或本地测试。',
    )

    return parser


def get_foods(input_file: Optional[str], source: str) -> List[Dict]:
    """优先从文件加载数据，否则执行采集。"""
    if input_file:
        logger.info("从文件加载数据：%s", input_file)
        return load_foods_from_file(input_file)

    logger.info("开始采集数据，source=%s", source)
    return fetch_food_data(source=source)


def main() -> None:
    """命令行入口。"""
    parser = build_parser()
    args = parser.parse_args()

    if not any([args.crawl, args.recommend, args.push]):
        parser.print_help()
        return

    try:
        foods = get_foods(args.input, args.source)
        output_data = foods

        if args.recommend:
            logger.info("开始计算 Top-%s 推荐结果", args.top_n)
            output_data = recommend(
                foods,
                top_n=args.top_n,
                prefer=args.prefer,
                include_closed=args.include_closed,
            )

        if args.crawl or args.recommend:
            if args.output:
                save_json(output_data, args.output)
            else:
                print_json(output_data)

        if args.push:
            if args.recommend:
                target_url = args.url or DEFAULT_RECOMMEND_URL
                logger.info("推送推荐结果到接口：%s", target_url)
                success = push_to_java(
                    output_data,
                    server_url=target_url,
                    wrap_payload=args.wrap_payload,
                )
            else:
                target_url = args.url or DEFAULT_SYNC_URL
                logger.info("推送采集数据到接口：%s", target_url)
                success = push_to_java(
                    foods,
                    server_url=target_url,
                    wrap_payload=args.wrap_payload,
                )

            if not success:
                raise RuntimeError("数据推送失败，请检查 Java 后端服务或接口地址。")

    except Exception as exc:
        logger.exception("程序执行失败：%s", exc)


if __name__ == "__main__":
    main()
