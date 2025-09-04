package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDishPO;
import com.sky.entity.SetmealPO;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealMapper extends BaseMapper<SetmealPO> {




    Page<SetmealVO> page(Long categoryId, String name, Integer status);

//    @AutoFill(OperationType.INSERT)
//    void insert(SetmealPO setmealPO);

//    @Select("select * from setmeal where id = #{id}")
//    SetmealPO selectById(Integer id);


    @Update("update setmeal set status = #{status} where id = #{id}")
    void startOrStop(Integer status, Long id);

    void deleteByIds(List<Long> ids);

    @Select("select count(*) from setmeal where category_id= #{id}")
    Long countByCategoryId(Long id);
}
