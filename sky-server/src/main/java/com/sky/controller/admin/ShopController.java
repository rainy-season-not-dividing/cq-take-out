package com.sky.controller.admin;


import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sky.constant.ShopStatusConstant.*;


@RestController
@Slf4j
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/admin/shop/{status}")
    public Result<String> setShopStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态:{}",status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(SHOP_STATUS, status);
        return Result.success();
    }

    @GetMapping("/admin/shop/status")
    public Result<Integer> getShopStatus(){
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS);
        log.info("获取店铺营业状态:{}",shopStatus == 1 ? "营业中" : "打烊中");
        return Result.success(shopStatus);
    }
}
