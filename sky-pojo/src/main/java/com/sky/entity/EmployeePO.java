package com.sky.entity;

import lombok.Data;

@Data
public class EmployeePO {
    private Integer id;
    private String name;
    private String username;
    private String password;
    private String phone;
    private String sex;
    private String idNumber;
    private Long status;
    private String createTime;
    private String updateTime;
    private Long createUser;
    private Long updateUser;
}
