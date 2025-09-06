package com.sky.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RedisDataDTO<T> {
    private List<T> data;
    private LocalDateTime expireTime;
}
