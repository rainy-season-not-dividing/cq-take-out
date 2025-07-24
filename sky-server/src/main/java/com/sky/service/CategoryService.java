package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.entity.CategoryPO;
import com.sky.result.Result;
import com.sky.vo.CategoryVO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    // 修改分类
    void update(CategoryDTO categoryDTO);

    // 分页查询
    CategoryVO page(Integer page, Integer pageSize, String name, Integer type);

    //启停状态修改
    void startOrStop(Integer status, Long id);

    // 新增分类
    void add(CategoryDTO categoryDTO);

    //删除分类
    void delete(Long id);

    //分类查询
    List<CategoryPO> selectByType(Integer type);
}
