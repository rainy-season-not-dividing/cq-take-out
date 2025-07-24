package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.entity.CategoryPO;
import com.sky.mapper.CategoryMapper;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.vo.CategoryVO;
import com.sky.context.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImp implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void update(CategoryDTO categoryDTO) {
        //调用方法修改分类的信息
        categoryMapper.updateById(categoryDTO);
    }

    @Override
    public CategoryVO page(Integer page, Integer pageSize, String name, Integer type) {
        /*
        *未使用pageHelper
        //得到总记录数
        List<CategoryPO> records = categoryMapper.selectByNameAndType(name, type);
        //得到当前页的数据、封装成VO
        CategoryVO result = new CategoryVO();
        result.setTotal((long) records.size());
        result.setRecords(records.subList((page - 1) * pageSize, Math.min(page * pageSize, records.size())));
        //返回结果
        return result;
        * */
        //使用pageHelper简化
        //使用pageHelper
        PageHelper.startPage(page,pageSize);
        Page<CategoryPO> records = (Page<CategoryPO>) categoryMapper.selectByNameAndType(name, type);
        return new CategoryVO(records.getTotal(), records.getResult());

    }

    @Override
    public void startOrStop(Integer status, Long id) {
        categoryMapper.updateStatusById(id,status);
    }

    @Override
    public void add(CategoryDTO categoryDTO) {
        //dto 封装成 po
        CategoryPO categoryPo = CategoryPO.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .sort(categoryDTO.getSort())
                .type(categoryDTO.getType())
                .status(Long.valueOf(StatusConstant.DISABLE))     //默认为禁用
                .createTime(String.valueOf(LocalDateTime.now()))
                .updateTime(String.valueOf(LocalDateTime.now()))
                .createUser(BaseContext.getCurrentId())
                .updateUser(BaseContext.getCurrentId()).build();
        //插入表中
        categoryMapper.insert(categoryPo);
    }

    @Override
    public void delete(Long id) {
        // todo:补充异常处理，不存在并不会出错，但是可能该分类与菜品或者套餐有关联，所以不能删除
        //根据id删除分类
        categoryMapper.deleteById(id);
    }

    @Override
    public List<CategoryPO> selectByType(Integer type) {
        List<CategoryPO> categoryPOs = categoryMapper.selectByType(type);
        return categoryPOs;
    }
}
