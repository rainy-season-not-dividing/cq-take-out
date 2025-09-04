package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.dto.SetmealDTO;
import com.sky.entity.SetmealDishPO;
import com.sky.entity.SetmealPO;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImp extends ServiceImpl<SetmealMapper,SetmealPO> implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

//    @Autowired
//    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    SetmealDishService setmealDishService;



    @Override
    public PageResult<SetmealVO> page(Long categoryId, String name, Integer page, Integer pageSize, Integer status) {
//        PageHelper.startPage(page,pageSize);
        // 这种方式不好，包含多次调用mapper，数据库压力较大
//        Page<SetmealVO> records =  setmealMapper.page(categoryId, name, status);
        // mybatis-plus的分页插件
        Page<SetmealPO> pageRecords = new Page<>(page,pageSize);
        LambdaQueryWrapper<SetmealPO> queryWrapper = new LambdaQueryWrapper<SetmealPO>()
                .eq(categoryId!=null,SetmealPO::getCategoryId,categoryId)
                .eq(name!=null,SetmealPO::getName,name)
                .eq(status!=null,SetmealPO::getStatus,status);
        setmealMapper.selectPage(pageRecords,queryWrapper);
//        log.info("分页查询，结果：{}", pageRecords.getRecords());
        List<SetmealVO> setmealVOs = pageRecords.getRecords().stream()
                .map(setmealPO->{
                    SetmealVO setmealVO = new SetmealVO();
                    BeanUtils.copyProperties(setmealPO,setmealVO);
                    setmealVO.setCategoryName(categoryMapper.selectById(setmealPO.getCategoryId()).getName());
                    setmealVO.setSetmealDishes(setmealDishService.list(new LambdaQueryWrapper<SetmealDishPO>().eq(SetmealDishPO::getSetmealId,setmealPO.getId())).toArray(new SetmealDishPO[0]));
                    return setmealVO;
                })
                .collect(Collectors.toList());
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
        return new PageResult<>(pageRecords.getTotal(),setmealVOs);
    }

    @Override
    @Transactional
    public void add(SetmealDTO setmealDTO) {
        // 封装setmealPO
        SetmealPO setmealPO = new SetmealPO();
        BeanUtils.copyProperties(setmealDTO,setmealPO);
        // setmeal表新增
//        setmealMapper.insert(setmealPO);
        save(setmealPO);
        // setmeal_dish新增
//        setmealDTO.getSetmealDishes().forEach(
//                setmealDishDTO -> {
//                    setmealDishDTO.setSetmealId(setmealPO.getId());
//                });
        List<SetmealDishPO> setmealDishes =setmealDTO.getSetmealDishes().stream().map(setmealDishDTO->{
            SetmealDishPO setmealDishPO = new SetmealDishPO();
            BeanUtils.copyProperties(setmealDishDTO,setmealDishPO);
            setmealDishPO.setSetmealId(setmealPO.getId());
            return setmealDishPO;
        }).collect(Collectors.toList());
//        setmealDishMapper.insertBatch(setmealDTO.getSetmealDishes());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public SetmealVO getById(Integer id) {
        // setmeal
        SetmealPO setmealPO = setmealMapper.selectById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmealPO,setmealVO);
        //setmeal_dish
        SetmealDishPO[] setmealDishes = setmealDishService.list(new LambdaQueryWrapper<SetmealDishPO>().eq(SetmealDishPO::getSetmealId,id)).toArray(new SetmealDishPO[0]);
        setmealVO.setSetmealDishes(setmealDishes);
        setmealVO.setCategoryName(categoryMapper.selectById(setmealPO.getCategoryId()).getName());
        return setmealVO;
    }

    @Override
    public void update(SetmealDTO setmealSaveDTO) {
        SetmealPO setmealPO = new SetmealPO();
        BeanUtils.copyProperties(setmealSaveDTO,setmealPO);
        log.info("修改套餐，参数：{}",setmealPO);
//        setmealMapper.updateById(setmealPO);
        updateById(setmealPO);
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDishPO>().eq(SetmealDishPO::getSetmealId,setmealSaveDTO.getId()));
        List<SetmealDishPO> setmealDishes =setmealSaveDTO.getSetmealDishes().stream().map(setmealDishDTO->{
            SetmealDishPO setmealDishPO = new SetmealDishPO();
            BeanUtils.copyProperties(setmealDishDTO,setmealDishPO);
            setmealDishPO.setSetmealId(setmealPO.getId());
            return setmealDishPO;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        setmealMapper.startOrStop(status,id);
    }

    @Override
    public void delete(List<Long> ids) {
        if(ids==null || ids.size()<=0) return ;
        // 删除setmeal_dish中的关联菜品
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDishPO>().in(SetmealDishPO::getSetmealId,ids));
        // 删除套餐
        setmealMapper.deleteByIds(ids);
    }
}
