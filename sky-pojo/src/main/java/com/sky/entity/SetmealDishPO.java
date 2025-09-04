package com.sky.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("setmeal_dish")
public class SetmealDishPO {
    @TableId(type = IdType.AUTO,value = "id")
    private Long id;
    private Long setmealId;
    private Long dishId;
    private String name;
    private BigDecimal price;
    private Integer copies;


}
