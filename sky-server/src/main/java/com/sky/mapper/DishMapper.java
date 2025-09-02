package com.sky.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.DishPO;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    @AutoFill(OperationType.UPDATE)
    void update(DishPO dishPO);

    Page<DishVO> page(String name, Integer categoryId, Integer status);

    @Select("select * from dish where id = #{id}")
    DishPO selectById(Long id);

    @AutoFill(OperationType.INSERT)
    void insert(DishPO dishPO);

    @Select("select * from dish where category_id=#{categoryId}")
    List<DishPO> selectByCategoryId(Long categoryId);

    void deleteByIds(List<Long> idList);

    @Select("select count(*) from dish where category_id= #{id}")
    Long countByCategoryId(Long id);
}
