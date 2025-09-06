package com.sky.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserOrderVO {
    private Integer id;
    private BigDecimal orderAmount;
    private String orderNumber;
    private String orderTime;
}
