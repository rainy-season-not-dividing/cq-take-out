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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @PutMapping("/admin/dish")
    public Result<String> update(@RequestBody DishUpdateDTO dishUpdateDTO){
        log.info("修改菜品:{}",dishUpdateDTO);
        dishService.update(dishUpdateDTO);
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
        List<DishPO> list = dishService.selectByCategoryId(categoryId);
        return Result.success(list);
    }


    @PostMapping("/admin/dish")
    public Result<String> save(@RequestBody DishAddDTO dishAddDTO){
        log.info("新增菜品:{}",dishAddDTO);
        dishService.add(dishAddDTO);
        return Result.success();
    }

    @DeleteMapping("/admin/dish")
    public Result<String> delete(String ids){
        log.info("批量删除菜品:{}",ids);
        dishService.deleteByIds(ids);
        return Result.success();

    }
}
