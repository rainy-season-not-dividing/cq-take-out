package com.sky.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderSearchDTO {
    // 对应 beginTime 参数
    private LocalDate beginTime;
    // 对应 endTime 参数
    private LocalDate endTime;
    // 对应 number 参数
    private String number;
    // 对应 phone 参数
    private String phone;
    // 对应 status 参数
    private Integer status;
}
