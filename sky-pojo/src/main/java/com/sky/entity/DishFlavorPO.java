package com.sky.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dish_flavor")
public class DishFlavorPO {
    private Long id;
    private Long dishId;
    private String name;
    private String value;
}
