package com.fandian.controller;

import com.fandian.model.dto.Result;
import com.fandian.model.dto.SyncFoodRequest;
import com.fandian.service.FoodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 后台管理控制器（供 Python 爬虫调用）
 * 接口: POST /api/admin/sync
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Value("${sync.secret-key}")
    private String syncSecretKey;

    private final FoodService foodService;

    public AdminController(FoodService foodService) {
        this.foodService = foodService;
    }

    /**
     * 数据同步接口 - 接收爬虫推送的批量菜品数据
     * @param syncKey 安全校验密钥（Header）
     * @param foods   菜品列表 JSON
     * @return 同步结果
     */
    @PostMapping("/sync")
    public Result<?> syncFoods(
            @RequestHeader(value = "Sync-Key", required = false) String syncKey,
            @Valid @RequestBody List<SyncFoodRequest> foods) {

        // 安全校验
        if (syncKey == null || !syncKey.equals(syncSecretKey)) {
            log.warn("数据同步被拒绝: Sync-Key 无效");
            return Result.fail(403, "无权访问");
        }

        if (foods == null || foods.isEmpty()) {
            return Result.fail(400, "数据为空");
        }

        log.info("收到数据同步请求: {} 条菜品", foods.size());

        int count = foodService.syncFoods(foods);
        return Result.ok("同步完成，共 " + count + " 条");
    }
}
