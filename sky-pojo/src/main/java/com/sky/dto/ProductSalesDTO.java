package com.sky.dto;

import lombok.Data;

// 辅助DTO：存储“产品名+销量”（避免直接用POJO携带冗余字段）
@Data
public class ProductSalesDTO {
    private String productName; // 菜品/套餐名
    private Integer totalSales; // 总销量
}
