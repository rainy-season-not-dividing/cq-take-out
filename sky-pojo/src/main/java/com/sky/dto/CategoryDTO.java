package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data   // 自动生成getter和setter方法
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private Long sort;
    private Long type;
}
