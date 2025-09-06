package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishAddDTO;
import com.sky.dto.DishUpdateDTO;
import com.sky.entity.DishPO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DishService extends IService<DishPO> {

    // 修改菜品
    @Transactional
    void update(DishUpdateDTO dishUpdateDTO);

    // 分页擦查询菜品
    PageResult<DishVO> page(Integer page, Integer pageSize, String name, Integer categoryId, Integer status);

    // 根据id查询菜品
    DishVO getDishById(Long id);

    // 添加菜品
    @Transactional  //这个注解的作用是：如果添加菜品时出现异常，则回滚，确保数据一致性
    void add(DishAddDTO dishAddDTO);

    // 根据分类id查询菜品
    List<DishPO> selectByCategoryId(Long categoryId) throws InterruptedException;


    // 根据id批量删除菜品
    void deleteByIds(String ids);
}
