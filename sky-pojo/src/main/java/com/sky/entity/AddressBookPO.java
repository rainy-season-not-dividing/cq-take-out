package com.sky.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 地址簿实体类
 */
@Data
@TableName("address_book")
public class AddressBookPO {
    // 主键，自动递增  必须要这个注解，否则insert后不会自动回填
    @TableId(type = IdType.AUTO, value = "id")
    private Long id;
    // 用户id
    private Long userId;
    // 收货人
    private String consignee;
    // 性别
    private String sex;
    // 手机号
    private String phone;
    // 省份编码
    private String provinceCode;
    // 省份名称
    private String provinceName;
    // 城市编码
    private String cityCode;
    // 城市名称
    private String cityName;
    // 区县编码
    private String districtCode;
    // 区县名称
    private String districtName;
    // 详细地址
    private String detail;
    // 标签
    private String label;
    // 是否默认地址，0 否，1 是，默认 0
    private Integer isDefault;
}