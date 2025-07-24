package com.sky.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryPO {
    private Long id;
    private Long type;
    private String name;
    private Long sort;
    private Long status;
    private String createTime;
    private String updateTime;
    private Long createUser;
    private Long updateUser;
}
