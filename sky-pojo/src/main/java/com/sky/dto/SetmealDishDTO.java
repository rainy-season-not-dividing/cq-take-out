package com.sky.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SetmealDishDTO {
    private Integer copies;
    private Long dishId;
    private Integer id;
    private String name;
    private BigDecimal price;
    private Long setmealId;

}
