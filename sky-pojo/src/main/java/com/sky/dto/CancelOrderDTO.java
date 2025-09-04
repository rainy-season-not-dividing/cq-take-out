package com.sky.dto;


import lombok.Data;

@Data
public class CancelOrderDTO {
    String cancelReason;
    Integer id;
}
