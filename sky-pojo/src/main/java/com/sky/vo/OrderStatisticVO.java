package com.sky.vo;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderStatisticVO {
    Integer confirmed;
    Integer deliveryInProgress;
    Integer toBeConfirmed;
}
