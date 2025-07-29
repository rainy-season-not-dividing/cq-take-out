package com.sky.vo;

import com.sky.entity.DishFlavorPO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DishVO {
    private Long id;
    private String name;
    private Long categoryId;
    private BigDecimal price;
    private String image;
    private String description;
    private Integer status;
    private LocalDateTime updateTime;
    private String categoryName;

    private DishFlavorPO[] flavors;
}
