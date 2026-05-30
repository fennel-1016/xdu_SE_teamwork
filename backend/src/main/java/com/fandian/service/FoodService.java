package com.fandian.service;

import com.fandian.model.dto.FoodDTO;
import com.fandian.model.dto.SyncFoodRequest;
import com.fandian.model.entity.Food;
import com.fandian.repository.FoodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜品服务 - 处理随机抽选和数据同步
 */
@Service
public class FoodService {

    private static final Logger log = LoggerFactory.getLogger(FoodService.class);

    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    /**
     * 随机抽取一道菜品
     * @return 菜品 DTO，数据库为空时返回 null
     */
    public FoodDTO getRandomFood() {
        Food food = foodRepository.findRandomOne();
        if (food == null) {
            return null;
        }
        return new FoodDTO(
                food.getFoodName(),
                food.getPrice(),
                food.getCanteenName(),
                food.getWindowNo()
        );
    }

    /**
     * 批量同步菜品数据（爬虫推送）
     * 采用"存在则更新，不存在则插入"策略
     *
     * @param foods 爬虫推送的菜品列表
     * @return 成功导入的数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int syncFoods(List<SyncFoodRequest> foods) {
        int successCount = 0;
        LocalDateTime now = LocalDateTime.now();

        for (SyncFoodRequest item : foods) {
            try {
                foodRepository.upsert(
                        item.getName(),
                        item.getPrice(),
                        item.getCanteen(),
                        item.getWindow(),
                        now
                );
                successCount++;
            } catch (Exception e) {
                log.error("同步菜品失败: name={}", item.getName(), e);
                throw e; // 事务回滚
            }
        }

        log.info("数据同步完成: 成功 {} / 总数 {}", successCount, foods.size());
        return successCount;
    }

    /**
     * 获取菜品总数
     */
    public long getFoodCount() {
        return foodRepository.count();
    }
}
