package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.CategoryConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryDTO;
import com.sky.entity.CategoryPO;
import com.sky.entity.DishPO;
import com.sky.entity.SetmealPO;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.vo.CategoryVO;
import com.sky.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImp extends ServiceImpl<CategoryMapper,CategoryPO> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void update(CategoryDTO categoryDTO) {
        //调用方法修改分类的信息
        log.info("修改分类，参数：{}",categoryDTO);
        CategoryPO categoryPO = new CategoryPO();
        BeanUtils.copyProperties(categoryDTO,categoryPO);
        categoryMapper.updateById(categoryPO);
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
        /*
        使用pageHelper简化
        //使用pageHelper
        PageHelper.startPage(page,pageSize);
        Page<CategoryPO> records = categoryMapper.selectByNameAndType(name, type);
        return new CategoryVO(records.getTotal(), records.getResult());
        */

        /*
          使用mybatis-plus的分页插件
         */
        // 1、构造wrapper
        LambdaQueryWrapper<CategoryPO> wrapper = new LambdaQueryWrapper<CategoryPO>()
                .like(name!=null,CategoryPO::getName,name)
                .eq(type!=null, CategoryPO::getType,type);
        // 2、分页查询
        Page<CategoryPO> resultPage = new Page<>(page,pageSize);
        categoryMapper.selectPage(resultPage,wrapper);
        // 3、封装VO
        CategoryVO categoryVO = new CategoryVO(resultPage.getTotal(), resultPage.getRecords());

        // 4、返回结果
        return categoryVO;
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
                .status(StatusConstant.DISABLE)  //默认为禁用
//                .createTime(String.valueOf(LocalDateTime.now()))
//                .updateTime(String.valueOf(LocalDateTime.now()))
//                .createUser(BaseContext.getCurrentId())
//                .updateUser(BaseContext.getCurrentId())
                .build();
        //插入表中
        save(categoryPo);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 1、查询当前分类是否关联了菜品
//        Long count = dishMapper.countByCategoryId(id);
        Long count = dishMapper.selectCount(new LambdaQueryWrapper<DishPO>().eq(DishPO::getCategoryId,id));
        if(count>0){
            throw new DeletionNotAllowedException(CategoryConstant.CATEGORY_IS_RELATED_TO_DISH);
        }
        dishMapper.delete(new LambdaQueryWrapper<DishPO>().eq(DishPO::getCategoryId,id));
        // 2、查询当前分类是否关联了套餐
        count = setmealMapper.countByCategoryId(id);
        if(count>0){
            throw new DeletionNotAllowedException(CategoryConstant.CATEGORY_IS_RELATED_TO_SETMEAL);
        }
        setmealMapper.delete(new LambdaQueryWrapper<SetmealPO>().eq(SetmealPO::getCategoryId,id));
        //根据id删除分类
        // 自定义方法
//        categoryMapper.deleteById(id);
        // mybatis-plus
        removeById(id);
    }

    @Override
    public List<CategoryPO> selectByType(Integer type) {
//        List<CategoryPO> categoryPOs = categoryMapper.selectByType(type);
        List<CategoryPO> categoryPOs = list(new LambdaQueryWrapper<CategoryPO>().eq(CategoryPO::getType,type));
        return categoryPOs;
    }
}
