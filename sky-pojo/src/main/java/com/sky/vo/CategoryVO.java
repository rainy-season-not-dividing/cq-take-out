package com.sky.vo;

import com.sky.entity.CategoryPO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryVO {
    private Long total;
    private List<CategoryPO> records;
}
