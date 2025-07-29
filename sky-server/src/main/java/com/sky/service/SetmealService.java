package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    // 套餐分页查询
    PageResult<SetmealVO> page(Long categoryId, String name, Integer page, Integer pageSize, Integer status);

    // 新增套餐
    void add(SetmealDTO setmealDTO);

    // 查询套餐--回显
    SetmealVO getById(Integer id);

    // 修改套餐
    void update(SetmealDTO setmealSaveDTO);

    // 套餐状态修改
    void startOrStop(Integer status, Long id);

    // 批量删除套餐
    void delete(List<Long> ids);
}
