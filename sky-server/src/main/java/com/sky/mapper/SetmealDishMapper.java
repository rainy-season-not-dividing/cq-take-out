package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.dto.SetmealDishDTO;
import com.sky.entity.SetmealDishPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDishPO> {
    void insertBatch(List<SetmealDishDTO> setmealDishes);

    @Select("select * from setmeal_dish where setmeal_id=#{setmealId}")
    SetmealDishPO[] selectByDishId(Integer id);


    void deleteBySetmealIds(List<Long> id);

    @Delete("delete from setmeal_dish where setmeal_id=#{setmealId}")
    void deleteBySetmealId(Long id);

    List<SetmealDishPO> selectByDishIds(List<Long> idList);
}
