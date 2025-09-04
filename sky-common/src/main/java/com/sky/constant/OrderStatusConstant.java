package com.sky.constant;

public class OrderStatusConstant {
    public static final int PAYMENT_WAITING = 1;  //待付款
    public static final int PICK_UP_WAITING =2; // 待接单
    public static final int DELEVERY_WAITING = 3;    // 待派送/接单
    public static final int DELEVERY_PROGRESS = 4;  // 派送中
    public static final int COMPLETED = 5; // 完成
    public static final int CANCELED = 6;   // 取消
    public static final int REJECTED = 7; // 拒绝-退款


}
