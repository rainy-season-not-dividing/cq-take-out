package com.sky.controller.admin;

import com.sky.dto.DishAddDTO;
import com.sky.dto.DishUpdateDTO;
import com.sky.entity.DishPO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
public class DishController {
    // todo:改成Redis + MyBatis-Plus

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/admin/dish")
    public Result<String> update(@RequestBody DishUpdateDTO dishUpdateDTO){
        log.info("修改菜品:{}",dishUpdateDTO);
        dishService.update(dishUpdateDTO);
//        clearCache("category_"+dishUpdateDTO.getCategoryId());
        return Result.success();
    }

    @GetMapping("/admin/dish/page")
    public Result<PageResult<DishVO>> page(Integer page, Integer pageSize, String name, Integer categoryId, Integer status){
        log.info("分页查询,参数:page={},pageSize={},name={},categoryId={},status={}",page,pageSize,name,categoryId,status);
        PageResult<DishVO> pageResult = dishService.page(page, pageSize, name,categoryId,status);
        return Result.success(pageResult);
    }

    @GetMapping("/admin/dish/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品:{}",id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    @GetMapping("/admin/dish/list")
    public Result<List<DishPO>> getList(Long categoryId){
        log.info("查询分类id查询菜品列表:{}",categoryId);
        // 采用redis处理高并发问题
//        String key = "category_"+categoryId;
//        List<DishPO> list = (List<DishPO>) redisTemplate.opsForValue().get(key);
//        if(list != null && list.size() > 0){
//            return Result.success(list);
//        }
//        list = dishService.selectByCategoryId(categoryId);
//        redisTemplate.opsForValue().set(key,list);
        // 直接通过mysql数据库来实现
        List<DishPO> list = dishService.selectByCategoryId(categoryId);
        return Result.success(list);
    }


    @PostMapping("/admin/dish")
    public Result<String> save(@RequestBody DishAddDTO dishAddDTO){
        log.info("新增菜品:{}",dishAddDTO);
        dishService.add(dishAddDTO);
//        clearCache(String.valueOf(dishAddDTO.getCategoryId()));
        return Result.success();
    }

    @DeleteMapping("/admin/dish")
    public Result<String> delete(String ids){
        log.info("批量删除菜品:{}",ids);
        dishService.deleteByIds(ids);
//        clearCache("category_*");
        return Result.success();
    }

//    private void clearCache(String pattern){
//        log.info("清理缓存：{}",pattern);
//        Set keys = redisTemplate.keys(pattern);
//        redisTemplate.delete(keys);
//        log.info("清理完成");
//    }
//
//    @Scheduled(cron = "0 * * * * ? ")
//    private void clearCache(){
//        log.info("定时任务调度");
//        clearCache("category::*");
//    }
}
