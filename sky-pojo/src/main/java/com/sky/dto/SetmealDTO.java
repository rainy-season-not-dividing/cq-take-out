package com.sky.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SetmealDTO {
    private Long categoryId;
    private String description;
    private Long id;
    private String image;
    private String name;
    private BigDecimal price;
    private Integer status;
    private List<SetmealDishDTO> setmealDishes;
}
