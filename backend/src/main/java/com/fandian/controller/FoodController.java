package com.fandian.controller;

import com.fandian.model.dto.FoodDTO;
import com.fandian.model.dto.Result;
import com.fandian.service.FoodService;
import com.fandian.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 智能决策控制器
 * 接口: GET /api/food/random
 */
@RestController
@RequestMapping("/api/food")
public class FoodController {

    private static final Logger log = LoggerFactory.getLogger(FoodController.class);

    private final FoodService foodService;
    private final UserService userService;

    public FoodController(FoodService foodService, UserService userService) {
        this.foodService = foodService;
        this.userService = userService;
    }

    /**
     * 随机抽取一道菜品
     * @param authorization 请求头携带 OpenID
     * @return 随机菜品信息 或 错误提示
     */
    @GetMapping("/random")
    public Result<?> getRandomFood(@RequestHeader(value = "Authorization", required = false) String authorization) {
        log.info("收到随机抽选请求");

        // 校验用户身份
        if (authorization == null || authorization.isEmpty()) {
            return Result.fail(401, "请先登录");
        }
        if (!userService.isValidUser(authorization)) {
            return Result.fail(401, "用户身份无效，请重新登录");
        }

        // 执行随机抽选
        FoodDTO food = foodService.getRandomFood();

        if (food == null) {
            return Result.fail(404, "食堂暂无菜品，请联系管理员");
        }

        return Result.ok(food);
    }
}
