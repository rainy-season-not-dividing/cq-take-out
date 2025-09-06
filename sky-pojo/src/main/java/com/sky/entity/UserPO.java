package com.sky.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data // 该注解包含了 @Getter、@Setter、@ToString、@EqualsAndHashCode 等注解功能
@TableName("user")
public class UserPO {
    // 主键，bigint 类型（自动递增）
    @TableId(type = IdType.AUTO)
    private Long id;
    // 微信用户唯一标识，varchar(45) 类型
    private String openid;
    // 姓名，varchar(32) 类型
    private String name;
    // 手机号，varchar(11) 类型
    private String phone;
    // 性别，varchar(2) 类型
    private String sex;
    // 身份证号，varchar(18) 类型
    private String idNumber;
    // 头像，varchar(500) 类型
    private String avatar;
    // 创建时间，datetime 类型
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
