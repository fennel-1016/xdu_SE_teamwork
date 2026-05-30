package com.fandian.model.dto;

import java.math.BigDecimal;

/**
 * 菜品返回对象 - 返回给前端展示
 */
public class FoodDTO {

    private String name;
    private BigDecimal price;
    private String canteen;
    private String window;

    public FoodDTO() {}

    public FoodDTO(String name, BigDecimal price, String canteen, String window) {
        this.name = name;
        this.price = price;
        this.canteen = canteen;
        this.window = window;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCanteen() {
        return canteen;
    }

    public void setCanteen(String canteen) {
        this.canteen = canteen;
    }

    public String getWindow() {
        return window;
    }

    public void setWindow(String window) {
        this.window = window;
    }
}
