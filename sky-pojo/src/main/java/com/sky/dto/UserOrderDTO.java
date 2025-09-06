package com.sky.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * 用户订单数据传输对象
 */
@Data
public class UserOrderDTO {

    //todo：非空注解

    /**
     * 地址簿id
     */
    private Integer addressBookId;

    /**
     * 总金额
     */
    private Double amount;

    /**
     * 配送状态：1立即送出 0选择其他时间
     */
    private Integer deliveryStatus;

    /**
     * 预计送达时间
     */
    private String estimatedDeliveryTime;

    /**
     * 打包费
     */
    private Integer packAmount;

    /**
     * 付款方式
     */
    private Integer payMethod;

    /**
     * 备注
     */
    private String remark;

    /**
     * 餐具数量
     */
    private Integer tablewareNumber;

    /**
     * 餐具数量状态 1按餐量提供 0选择数量
     */
    private Integer tablewareStatus;
}
