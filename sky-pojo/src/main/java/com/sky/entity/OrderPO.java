package com.sky.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("orders")
@NoArgsConstructor
@AllArgsConstructor
public class OrderPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String number;
    // 订单状态 1待付款 2待派送 3已派送 4已完成 5已取消
    private Integer status;
    private Long userId;
    private Long addressBookId;
    // 下单时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime orderTime;
    private LocalDateTime checkoutTime;
    private Integer payMethod;
    private Integer payStatus;
    private BigDecimal amount;
    private String remark;
    private String phone;
    private String address;
    private String userName;
    private String consignee;
    private String cancelReason;
    private String rejectionReason;
    private LocalDateTime cancelTime;
    private LocalDateTime estimatedDeliveryTime;
    private Integer deliveryStatus;
    private LocalDateTime deliveryTime;
    private Integer packAmount;
    private Integer tablewareNumber;
    private Integer tablewareStatus;

}
