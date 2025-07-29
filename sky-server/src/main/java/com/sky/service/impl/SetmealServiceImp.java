package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.entity.SetmealDishPO;
import com.sky.entity.SetmealPO;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImp implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;



    @Override
    public PageResult<SetmealVO> page(Long categoryId, String name, Integer page, Integer pageSize, Integer status) {
        PageHelper.startPage(page,pageSize);
        Page<SetmealVO> records =  setmealMapper.page(categoryId, name, status);
        // 这种方式不好，包含多次调用mapper，数据库压力较大
        log.info("分页查询，结果：{}", records);
//        if (records==null || records.getTotal()<=0){
//            throw new NoRecordsException("没有数据记录");
//        }
//        List<SetmealVO> setmealVOs = records.getResult().stream().map(
//                setmealPO->{
//                    SetmealVO setmealVO = new SetmealVO();
//                    BeanUtils.copyProperties(setmealPO,setmealVO);
//                    setmealVO.setCategoryName(categoryMapper.selectById(setmealPO.getCategoryId()).getName());
//                    return setmealVO;
//                }
//        ).collect(Collectors.toList());
        return new PageResult<>(records.getTotal(),records.getResult());
    }

    @Override
    @Transactional
    public void add(SetmealDTO setmealDTO) {
        // 封装setmealPO
        SetmealPO setmealPO = new SetmealPO();
        BeanUtils.copyProperties(setmealDTO,setmealPO);
        // setmeal表新增
        setmealMapper.insert(setmealPO);
        // setmeal_dish新增
        setmealDTO.getSetmealDishes().forEach(
                setmealDishPO -> {
                    setmealDishPO.setSetmealId(setmealPO.getId());
                });
        setmealDishMapper.insertBatch(setmealDTO.getSetmealDishes());
    }

    @Override
    public SetmealVO getById(Integer id) {
        // setmeal
        SetmealPO setmealPO = setmealMapper.selectById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmealPO,setmealVO);
        //setmeal_dish
        SetmealDishPO[] setmealDishes = setmealDishMapper.selectByDishId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        setmealVO.setCategoryName(categoryMapper.selectById(setmealPO.getCategoryId()).getName());
        return setmealVO;
    }

    @Override
    public void update(SetmealDTO setmealSaveDTO) {
        SetmealPO setmealPO = new SetmealPO();
        BeanUtils.copyProperties(setmealSaveDTO,setmealPO);
        setmealMapper.updateById(setmealPO);
        setmealDishMapper.deleteBySetmealId(setmealSaveDTO.getId());
        setmealDishMapper.insertBatch(setmealSaveDTO.getSetmealDishes());
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        setmealMapper.startOrStop(status,id);
    }

    @Override
    public void delete(List<Long> ids) {
        // 删除setmeal_dish中的关联菜品
        setmealDishMapper.deleteBySetmealIds(ids);

        // 删除套餐
        setmealMapper.deleteByIds(ids);
    }
}
