package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;


    @GetMapping("/admin/setmeal/page")
    public Result<PageResult<SetmealVO>> page(Long categoryId, String name, @RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer pageSize, Integer status){
        log.info("分页查询，参数：{}", categoryId, name, page, pageSize, status);
        PageResult<SetmealVO> pageResult =setmealService.page(categoryId, name, page, pageSize, status);
        return Result.success(pageResult);
    }

    @PostMapping("/admin/setmeal")
    public Result<String> save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐:{}", setmealDTO);
        setmealService.add(setmealDTO);
        return Result.success();
    }

    @GetMapping("/admin/setmeal/{id}")
    public Result<SetmealVO> getById(@PathVariable Integer id){
        log.info("根据Id查询套餐:{}",id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @PutMapping("/admin/setmeal")
    public Result<String> update(@RequestBody SetmealDTO setmealSaveDTO){
        log.info("修改套餐:{}",setmealSaveDTO);
        setmealService.update(setmealSaveDTO);
        return Result.success();
    }

    @PostMapping("/admin/setmeal/status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("启用、禁用套餐:{},{}",status,id);
        setmealService.startOrStop(status,id);
        return Result.success();
    }

    @DeleteMapping("/admin/setmeal")
    @Transactional
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除套餐:{}",ids);
        setmealService.delete(ids);
        return Result.success();
    }


}
