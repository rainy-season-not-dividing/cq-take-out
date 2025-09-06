package com.sky.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RedisDataDTO<T> {
    private T data;
    private LocalDateTime expireTime;
}
