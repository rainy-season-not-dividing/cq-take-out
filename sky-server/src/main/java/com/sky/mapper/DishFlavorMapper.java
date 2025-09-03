package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.DishFlavorPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavorPO> {
    @Delete("delete from dish_flavor where dish_id=#{id}")
    void deleteByDishId(Long id);


    void insertBatch (DishFlavorPO[] flavor);

    @Select("select * from dish_flavor where dish_id=#{id}")
    DishFlavorPO[] selectByDishId(Long id);
}
