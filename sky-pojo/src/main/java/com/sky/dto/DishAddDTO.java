package com.sky.dto;

import com.sky.entity.DishFlavorPO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishAddDTO{

    private Long categoryId;
    private String description;
    private DishFlavorPO[] flavors;

    private Long id;
    private String image;
    private String name;
    private BigDecimal price;
    private Integer status;

}
