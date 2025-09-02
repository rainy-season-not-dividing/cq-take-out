package com.sky.config;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sky.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 全局字段自动填充处理器
 * 用于自动填充创建时间、更新时间等公共字段
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject){
            // 1、填充创建时间（字段名需与PO类中一致，如“createTime”
            strictInsertFill(
                    metaObject,
                    "createTime",// PO类中的字段名
                    LocalDateTime.class,// 字段类型
                    LocalDateTime.now()// 填充值：当前时间
            );

            // 2、插入时也填充更新时间（确保新增数据时updateTime有值
            strictInsertFill(
                    metaObject,
                    "updateTime",// PO类中的字段名
                    LocalDateTime.class,// 字段类型
                    LocalDateTime.now()// 填充值：当前时间
            );

            // 3、设置当前操作用户
            Long currentId = BaseContext.getCurrentId();
            strictInsertFill(
                    metaObject,
                    "createUser",// PO类中的字段名
                    Long.class,// 字段类型
                    currentId// 填充值：当前用户id
            );

            // 4、更新时也有
            strictInsertFill(
                    metaObject,
                    "updateUser",// PO类中的字段名
                    Long.class,// 字段类型
                    currentId// 填充值：当前用户id
            );
        }

    @Override
    public void updateFill(MetaObject metaObject){
        // 1、填充创建时间（字段名需与PO类中一致，如“createTime”
        strictUpdateFill(
                metaObject,
                "updateTime",// PO类中的字段名
                LocalDateTime.class,// 字段类型
                LocalDateTime.now()// 填充值：当前时间
        );
        // 2、当前操作用户
        Long currentId = BaseContext.getCurrentId();
        log.info("当前用户id:{}", currentId);
        strictUpdateFill(
                metaObject,
                "updateUser",// PO类中的字段名
                Long.class,// 字段类型
                currentId// 填充值：当前用户id
        );
    }


}
