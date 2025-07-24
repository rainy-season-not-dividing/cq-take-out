package com.sky.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeePageDTO {
    private String name;
    private Integer page;
    private Integer pageSize;
}
