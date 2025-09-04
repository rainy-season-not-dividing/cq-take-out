package com.sky.vo;

import com.sky.entity.OrderDetailPO;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单VO（用于接口返回/接收订单相关数据，对应文档字段定义）
 */
@Data
public class OrderDetailsVO {

    /**
     * 收货地址
     */
    private String address;

    /**
     * 地址簿ID（格式：int64，对应Java Long类型）
     */
    private Long addressBookId;

    /**
     * 订单金额（数字类型，对应Java BigDecimal避免精度丢失）
     */
    private BigDecimal amount;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 取消时间（格式：date-time，对应Java LocalDateTime类型）
     */
    private LocalDateTime cancelTime;

    /**
     * 支付时间（格式：date-time，对应Java LocalDateTime类型）
     */
    private LocalDateTime checkoutTime;

    /**
     * 收货人
     */
    private String consignee;

    /**
     * 配送状态（格式：int32，对应Java Integer类型）
     * 示例：1-立即送出  0-选择具体的时间
     */
    private Integer deliveryStatus;

    /**
     * 送达时间（格式：date-time，对应Java LocalDateTime类型）
     */
    private LocalDateTime deliveryTime;

    /**
     * 预计送达时间（格式：date-time，对应Java LocalDateTime类型）
     */
    private LocalDateTime estimatedDeliveryTime;

    /**
     * 订单ID（格式：int64，对应Java Long类型）
     */
    private Long id;

    /**
     * 订单编号（如：2024090412345678）
     */
    private String number;

    /**
     * 订单明细列表（item类型为对象，对应内部类OrderDetailVO）
     */
    private List<OrderDetailPO> orderDetailList;

    /**
     * 订单菜品信息（通常用于特殊格式展示，如"宫保鸡丁x1,鱼香肉丝x2"）
     */
    private String orderDishes;

    /**
     * 下单时间（格式：date-time，对应Java LocalDateTime类型）
     */
    private LocalDateTime orderTime;

    /**
     * 包装费（格式：int32，对应Java Integer类型）
     */
    private Integer packAmount;

    /**
     * 支付方式（格式：int32，对应Java Integer类型）
     * 示例：1-微信支付，2-支付宝支付
     */
    private Integer payMethod;

    /**
     * 支付状态（格式：int32，对应Java Integer类型）
     * 示例：0-未支付，1-已支付 2-退款
     */
    private Integer payStatus;

    /**
     * 收货人手机号
     */
    private String phone;

    /**
     * 拒单原因
     */
    private String rejectionReason;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 订单状态（格式：int32，对应Java Integer类型）
     * 示例：1-待付款， 2-待接单， 3-待派送，4-派送中，5-已完成，6-已取消， 7-已退款
     */
    private Integer status;

    /**
     * 餐具数量（格式：int32，对应Java Integer类型）
     */
    private Integer tablewareNumber;

    /**
     * 餐具状态（格式：int32，对应Java Integer类型）
     * 示例：0-选择在具体数量，1-按餐量提供
     */
    private Integer tablewareStatus;

    /**
     * 用户ID（格式：int64，对应Java Long类型）
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

}
