package com.fandian.repository;

import com.fandian.model.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {

    /**
     * 随机抽取一条菜品
     */
    @Query(value = "SELECT * FROM t_food ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Food findRandomOne();

    /**
     * UPSERT 操作：存在则更新，不存在则插入
     */
    @Modifying
    @Query(value = "INSERT INTO t_food (food_name, price, canteen_name, window_no, sync_time) " +
                   "VALUES (:name, :price, :canteen, :window, :syncTime) " +
                   "ON DUPLICATE KEY UPDATE price = :price, canteen_name = :canteen, " +
                   "window_no = :window, sync_time = :syncTime", nativeQuery = true)
    int upsert(@Param("name") String name,
               @Param("price") BigDecimal price,
               @Param("canteen") String canteen,
               @Param("window") String window,
               @Param("syncTime") LocalDateTime syncTime);
}
