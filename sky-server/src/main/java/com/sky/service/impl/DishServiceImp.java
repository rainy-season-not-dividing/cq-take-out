package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.DishConstant;
import com.sky.dto.DishAddDTO;
import com.sky.dto.DishUpdateDTO;
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
public class DishServiceImp implements DishService {

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
        dishMapper.update(dishPO);
        //修改dish_flavor表（先删后增）
        if (flavors != null && flavors.length > 0) {
            // 删除
            dishFlavorMapper.deleteByDishId(dishUpdateDTO.getId());
            // 添加
            for (DishFlavorPO flavor : flavors) {
                flavor.setDishId(dishPO.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult<DishVO> page(Integer page, Integer pageSize, String name, Integer categoryId, Integer status) {
//        // 设置分页参数并执行查询
        PageHelper.startPage(page,pageSize);
        Page<DishVO> records =  dishMapper.page(name, categoryId, status);
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
        return new PageResult<>(records.getTotal(), records.getResult());
    }

    @Override
    public DishVO getById(Long id) {
        DishPO dishPO = dishMapper.selectById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dishPO,dishVO);
        dishVO.setCategoryName(categoryMapper.selectById(dishPO.getCategoryId()).getName());
        dishVO.setFlavors(dishFlavorMapper.selectByDishId(id));
        return dishVO;
    }

    @Override
    @CacheEvict(cacheNames="category",key = "#dishAddDTO.categoryId")
    public void add(DishAddDTO dishAddDTO) {
        // 添加菜品
        DishPO dishPO = new DishPO();
        BeanUtils.copyProperties(dishAddDTO,dishPO);
        dishMapper.insert(dishPO);

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
        return dishMapper.selectByCategoryId(categoryId);
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
        List<SetmealDishPO> setmealDishPOs = setmealDishMapper.selectByDishIds(idList);
        if (setmealDishPOs != null && !setmealDishPOs.isEmpty()){
            throw new DeletionNotAllowedException(DishConstant.DISH_IS_RELATED_TO_SETMEAL);
        }
        //如果未关联，则可以删除菜品
        dishMapper.deleteByIds(idList);
    }
}
