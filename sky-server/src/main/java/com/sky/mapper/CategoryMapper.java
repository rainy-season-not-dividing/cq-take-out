package com.sky.mapper;

import com.sky.dto.CategoryDTO;
import com.sky.entity.CategoryPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    void updateById(CategoryDTO categoryDTO);

    List<CategoryPO> selectByNameAndType(String name, Integer type);

    @Update("update category set status=#{status} where id = #{id}")
    void updateStatusById(Long id, Integer status);


    void insert(CategoryPO categoryPo);

    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);

    @Select("select * from category where type = #{type}")
    List<CategoryPO> selectByType(Integer type);
}
