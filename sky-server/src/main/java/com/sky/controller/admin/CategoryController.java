package com.sky.controller.admin;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sky.dto.CategoryDTO;
import com.sky.entity.CategoryPO;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class CategoryController {
    // 优化，缓存提高查询效率

    @Autowired
    private CategoryService categoryService;

    //修改分类
    @PutMapping("/admin/category")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类，参数：{}",categoryDTO);
        CategoryPO categoryPO = new CategoryPO();
        BeanUtils.copyProperties(categoryDTO,categoryPO);
        categoryService.updateById(categoryPO);
        return Result.success();
    }

    //分类的分页查询
    @GetMapping("/admin/category/page")
    public Result<CategoryVO> page(Integer page, Integer pageSize, String name, Integer type){
        log.info("分页查询，参数：page={},pageSize={},name={},type={}",page,pageSize,name,type);
        CategoryVO result = categoryService.page(page, pageSize, name, type);
        return Result.success(result);
    }

    //启用、禁用分类
    @PostMapping("/admin/category/status/{status}")
    public Result<String> startOrStop(@PathVariable Integer status,@RequestParam Long id){
        log.info("启用、禁用分类，状态：{}",status);
//        categoryService.startOrStop(status,id);
        // 构造 updatewrapper
        UpdateWrapper<CategoryPO> updateWrapper = new UpdateWrapper<CategoryPO>()
                .eq("id",id)
                .set("status",status);
        // 调用updateById函数
        categoryService.update(updateWrapper);
        return Result.success();
    }

    //新增分类
    @PostMapping("/admin/category")
    public Result<String> add(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}",categoryDTO);
        categoryService.add(categoryDTO);
        return Result.success();
    }

    //删除分类
    @DeleteMapping("/admin/category")
    public Result<String> delete(@RequestParam Long id){
        log.info("删除分类,id为：{}",id);
        categoryService.delete( id);
        return Result.success();
    }

    //查询分类
    @GetMapping("/admin/category/list")
    public Result<List<CategoryPO>> list(Integer type){
        log.info("查询分类，type={}",type);
        List<CategoryPO> list = categoryService.selectByType( type);
        return Result.success( list);
    }

}
