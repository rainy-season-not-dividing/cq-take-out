package com.sky.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("setmeal_dish")
public class SetmealDishPO {
    private Long id;
    private Long setmeaId;
    private Long dishId;
    private String name;
    private BigDecimal price;
    private Integer copies;
}
