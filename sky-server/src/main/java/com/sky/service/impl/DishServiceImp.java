package com.sky.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.DishConstant;
import com.sky.dto.DishAddDTO;
import com.sky.dto.DishUpdateDTO;
import com.sky.entity.CategoryPO;
import com.sky.entity.DishFlavorPO;
import com.sky.entity.DishPO;
import com.sky.entity.SetmealDishPO;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImp extends ServiceImpl<DishMapper,DishPO> implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;




    @Override
    @CachePut(cacheNames = "category", key = "#dishUpdateDTO.categoryId")
    public void update(DishUpdateDTO dishUpdateDTO) {

        // 封装DishPO类
        DishPO dishPO = new DishPO();
        BeanUtils.copyProperties(dishUpdateDTO,dishPO);
        // 封装DishFlavorPO类
        DishFlavorPO[] flavors = dishUpdateDTO.getFlavors();
        // 修改dish表
        // 原方法
//        dishMapper.update(dishPO);
        // mybatis-plus
        dishMapper.updateById(dishPO);
        //修改dish_flavor表（先删后增）
        if (flavors != null && flavors.length > 0) {
            // 删除
            // 自定义方法
//            dishFlavorMapper.deleteByDishId(dishUpdateDTO.getId());
            // mybatis-plus
            dishFlavorMapper.deleteById(dishUpdateDTO.getId());
            // 添加
            for (DishFlavorPO flavor : flavors) {
                flavor.setDishId(dishPO.getId());
            }
            // 自定义方法
            dishFlavorMapper.insertBatch(flavors);
            // mybatis-plus
//            dishFlavorMapper.insertBatchSomeColumn(Arrays.asList(flavors));
        }
    }

    @Override
    public PageResult<DishVO> page(Integer page, Integer pageSize, String name, Integer categoryId, Integer status) {
//        // 设置分页参数并执行查询
//        PageHelper.startPage(page,pageSize);
//        Page<DishVO> records =  dishMapper.page(name, categoryId, status);
//        log.info("分页查询结果：{}", records);
//        List<DishVO> dishVOs = records.getResult().stream()
//                .map(dishPO -> {
//                    DishVO dishVO = new DishVO();
//                    BeanUtils.copyProperties(dishPO,dishVO);
//                    dishVO.setCategoryName(categoryMapper.selectById(dishPO.getCategoryId()).getName());
//                    return dishVO;
//                })
//                .collect(Collectors.toList());
//        return new PageResult(records.getTotal(), dishVOs);
        Page<DishPO> pageRecords = new Page<>(page,pageSize);
        LambdaQueryWrapper<DishPO> queryWrapper = new LambdaQueryWrapper<DishPO>()
                .eq(name!=null,DishPO::getName,name)
                .eq(categoryId!=null,DishPO::getCategoryId,categoryId)
                .eq(status!=null,DishPO::getStatus,status);

        dishMapper.selectPage(pageRecords,queryWrapper);
        List<DishVO> dishVOs = pageRecords.getRecords().stream()
                .map(dishPO->{
                    DishVO dishVO = new DishVO();
                    BeanUtils.copyProperties(dishPO,dishVO);
                    LambdaQueryWrapper<CategoryPO> categoryQueryWrapper = new LambdaQueryWrapper<CategoryPO>()
                            .eq(CategoryPO::getId,dishPO.getCategoryId())
                            .select(CategoryPO::getName);
                    dishVO.setCategoryName(categoryMapper.selectOne(categoryQueryWrapper).getName());
                    // 添加菜品-口味
                    // todo:换成mybatis-plus后重写
                    dishVO.setFlavors(dishFlavorMapper.selectByDishId(dishPO.getId()));
                    return dishVO;
                })
                .collect(Collectors.toList());
        return new PageResult<>(pageRecords.getTotal(),dishVOs);
    }

    @Override
    public DishVO getDishById(Long id) {
//        DishPO dishPO = dishMapper.selectById(id);
//        DishVO dishVO = new DishVO();
//        BeanUtils.copyProperties(dishPO,dishVO);
//        dishVO.setCategoryName(categoryMapper.selectById(dishPO.getCategoryId()).getName());
//        dishVO.setFlavors(dishFlavorMapper.selectByDishId(id));
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(getById(id),dishVO);
        dishVO.setCategoryName(categoryMapper.selectById(dishVO.getCategoryId()).getName());
        dishVO.setFlavors(dishFlavorMapper.selectList(new LambdaQueryWrapper<DishFlavorPO>().eq(DishFlavorPO::getDishId,id)).toArray(new DishFlavorPO[0]));
        return dishVO;
    }

    @Override
    @CacheEvict(cacheNames="category",key = "#dishAddDTO.categoryId")
    public void add(DishAddDTO dishAddDTO) {
        // 添加菜品
        DishPO dishPO = BeanUtil.copyProperties(dishAddDTO,DishPO.class);

       save(dishPO);

        // 添加菜品-口味
        DishFlavorPO[] flavors = dishAddDTO.getFlavors();
        if (flavors != null && flavors.length > 0) {
            for (DishFlavorPO flavor : flavors) {
                flavor.setDishId(dishPO.getId());
            }
        }
        dishFlavorMapper.insertBatch(flavors);
    }

    @Override
    @Cacheable(cacheNames="category", key = "#categoryId")
    public List<DishPO> selectByCategoryId(Long categoryId) {
        // 自定义方法
//        return dishMapper.selectByCategoryId(categoryId);
        // mybatis-plus
        return list(new LambdaQueryWrapper<DishPO>().eq(DishPO::getCategoryId,categoryId));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames="category",allEntries = true)
    public void deleteByIds(String ids) {
        // ids转换列表
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        // 检查是否关联套餐了，如果关联了则无法删除，抛出异常
        // 自定义方法
//        List<SetmealDishPO> setmealDishPOs = setmealDishMapper.selectByDishIds(idList);
        // mybatis-plus
        List<SetmealDishPO> setmealDishPOs = setmealDishMapper.selectList(new LambdaQueryWrapper<SetmealDishPO>().in(SetmealDishPO::getDishId,idList));
        if (setmealDishPOs != null && !setmealDishPOs.isEmpty()){
            throw new DeletionNotAllowedException(DishConstant.DISH_IS_RELATED_TO_SETMEAL);
        }
        //如果未关联，则可以删除菜品
        // 自定义方法
//        dishMapper.deleteByIds(idList);
        // mybatis-plus
        removeByIds(idList);
    }
}
