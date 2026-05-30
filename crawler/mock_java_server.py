"""模拟 Java Spring Boot 后端接口，方便本地测试推送。"""

from flask import Flask, jsonify, request


app = Flask(__name__)


def _count_foods(data):
    if isinstance(data, list):
        return len(data)
    if isinstance(data, dict):
        return len(data.get("foods", []))
    return 0


@app.route("/api/admin/sync", methods=["POST"])
@app.route("/api/foods/sync", methods=["POST"])
def sync_foods():
    data = request.get_json()
    print("收到同步数据：", data)
    print("Sync-Key：", request.headers.get("Sync-Key"))
    return jsonify({"code": 200, "msg": "sync ok", "data": {"count": _count_foods(data)}})


@app.route("/api/recommend", methods=["POST"])
def sync_recommend():
    data = request.get_json()
    print("收到推荐数据：", data)
    return jsonify({"code": 200, "msg": "recommend ok", "data": {"count": _count_foods(data)}})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080, debug=True)
