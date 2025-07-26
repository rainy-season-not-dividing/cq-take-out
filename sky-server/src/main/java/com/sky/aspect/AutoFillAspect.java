package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行数据填充");

        // 获取到当前被拦截的方法上的数据库操作类型
        // 1、获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        // 2、获取签名上的注解类
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得当前被拦截的方法上的数据库操作类型
        // 3、获取注解类的值，可见注解类中可以有多个值存储
        OperationType operationType = autoFill.value();//获得数据库操作类型

        // 获取得到当前被拦截的方法的参数，即实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        // 约定第一个参数数据库表实体类
        Object entity = args[0];

        // 准备赋值的数据
        //1、时间
        LocalDateTime now = LocalDateTime.now();
        if (BaseContext.getCurrentId() == null) {
            throw new RuntimeException("当前用户id为空，请重新登录");
        }
        //2、拿出线程中的操作用户的id，  这是在拦截器中，通过请求token的JWT令牌解析得到的id，并存储在线程变量中
        long currentId = BaseContext.getCurrentId();
        log.info("当前用户id:{}", currentId);


        //根据当前不同的操作类型，为对应的属性通过反射来赋值
        if (operationType == OperationType.INSERT){
            try {
                // 下面这几个是赋值给实体类对象的方法，作用是获取得到对应的set方法
                // 1、获取方法的映射
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 2、通过反射为对象属性赋值
                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }


    }
}
