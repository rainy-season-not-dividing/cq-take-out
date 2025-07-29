package com.sky.entity;

import lombok.Data;

@Data
public class DishFlavorPO {
    private Long id;
    private Long dishId;
    private String name;
    private String value;
}
