package com.sky.vo;

import lombok.Data;

@Data
public class EmployeeLoginVO {
    private Integer id;
    private String name;
    private String token;
    private String userName;
}
