package com.sky.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.OrderStatusConstant;
import com.sky.dto.CancelOrderDTO;
import com.sky.dto.OrderSearchDTO;
import com.sky.dto.ProductSalesDTO;
import com.sky.dto.RejectOrderDTO;
import com.sky.entity.DishPO;
import com.sky.entity.OrderDetailPO;
import com.sky.entity.OrderPO;
import com.sky.entity.SetmealPO;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.service.OrderDetailService;
import com.sky.service.OrderService;
import com.sky.service.SetmealService;
import com.sky.vo.OrderDetailsVO;
import com.sky.vo.OrderStatisticVO;
import com.sky.vo.ReportStatisticTop;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderPO> implements OrderService {

    // 企业上直接在Service层一般是直接注入其它的Service而不是Mapper，高内聚，低耦合
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public OrderStatisticVO statistics() {
        // 1、confirmed
        int confirmed = (int) count(new LambdaQueryWrapper<>(OrderPO.class).eq(OrderPO::getStatus, OrderStatusConstant.DELEVERY_WAITING));
        // 2、deliveryInProgress
        int deliveryInProgress = (int) count(new LambdaQueryWrapper<>(OrderPO.class).eq(OrderPO::getStatus, OrderStatusConstant.DELEVERY_PROGRESS));
        // 3、toBeConfirmed
        int toBeConfirmed =(int) count(new LambdaQueryWrapper<>(OrderPO.class).eq(OrderPO::getStatus, OrderStatusConstant.PICK_UP_WAITING));
        // 4、封装
        return new OrderStatisticVO(confirmed, deliveryInProgress, toBeConfirmed);
    }

    @Override
    public void cancelOrder(CancelOrderDTO cancelOrderDTO) {
        // 1、订单详细表 删除对应订单
        orderDetailService.remove(new LambdaQueryWrapper<>(OrderDetailPO.class).eq(OrderDetailPO::getOrderId, cancelOrderDTO.getId()));
        // 2、订单表 修改对应订单状态/信息
        OrderPO orderPO = BeanUtil.copyProperties(cancelOrderDTO, OrderPO.class);
        // 修改订单状态
        orderPO.setStatus(OrderStatusConstant.CANCELED);
        updateById(orderPO);
    }

    @Override
    public void complete(Long id) {
        // 1、修改订单状态--状态修改为完成
        updateById(OrderPO.builder().status(OrderStatusConstant.COMPLETED).id(id).build());
    }

    @Override
    public void rejection(RejectOrderDTO rejectOrderDTO) {
        // 修改订单状态--状态修改为拒绝
        updateById(OrderPO.builder().status(OrderStatusConstant.REJECTED).id(Long.valueOf(rejectOrderDTO.getId())).build());
    }

    @Override
    public void confirm(Integer id) {
        // 修改订单状态--状态修改为完成
        updateById(OrderPO.builder().status(OrderStatusConstant.DELEVERY_WAITING).id(Long.valueOf(id)).build());
    }

    @Override
    public OrderDetailsVO details(Long id) {
        // orders表 查询信息
        OrderPO orderPO = getById(id);
        // orders_detail表 获取信息
        List<OrderDetailPO> orderDetailPOList =orderDetailService.list(new LambdaQueryWrapper<OrderDetailPO>().eq(OrderDetailPO::getOrderId, id));
        // 获取具体的dish信息
//        String orderDishes = "";
//        orderDishes = orderDetailPOList.stream().map(orderDetailPO -> {
//            String orderDish = "";
//            // 1、dish
//            if(orderDetailPO.getDishId() != null){
//                orderDish = dishService.getById(orderDetailPO.getDishId()).getName()+"*"+orderDetailPO.getNumber();
//            }
//            // 2、setmeal
//            if(orderDetailPO.getSetmealId() != null){
//                orderDish = orderDish.concat(setmealService.getById(orderDetailPO.getSetmealId()).getName()+"*"+orderDetailPO.getNumber());
//            }
//            return orderDish;
//        }).collect(Collectors.joining(","));
        // 下面这个方法更好：若 orderDetailPOList 数量较多（如 100+），当前 “循环调用 getById()” 会产生 100 次数据库查询（N+1 问题），可改为 批量查询：
        // 批量收集dishId和setmealId
        List<Long> dishIds = orderDetailPOList.stream()
                .filter(detail -> detail.getDishId() != null)
                .map(OrderDetailPO::getDishId)
                .collect(Collectors.toList());
        List<Long> setmealIds = orderDetailPOList.stream()
                .filter(detail -> detail.getSetmealId() != null)
                .map(OrderDetailPO::getSetmealId)
                .collect(Collectors.toList());

// 批量查询，缓存名称（用Map避免重复查询）
        Map<Long, String> dishNameMap = dishService.listByIds(dishIds).stream()
                .collect(Collectors.toMap(DishPO::getId, DishPO::getName));
        Map<Long, String> setmealNameMap = setmealService.listByIds(setmealIds).stream()
                .collect(Collectors.toMap(SetmealPO::getId, SetmealPO::getName));

// 流式处理时直接从Map获取名称（无数据库查询）
        String orderDishes = orderDetailPOList.stream()
                .filter(detail -> detail.getDishId() != null || detail.getSetmealId() != null)
                .map(detail -> {
                    String name = dishNameMap.getOrDefault(detail.getDishId(), setmealNameMap.getOrDefault(detail.getSetmealId(), "未知商品"));
//                    Integer number = detail.getNumber() == null ? 0 : detail.getNumber();
                    return detail.getNumber() == null ? "" :name + "*" + detail.getNumber();
                })
                .collect(Collectors.joining(","));
        // 封装
        OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
        BeanUtil.copyProperties(orderPO, orderDetailsVO);
        orderDetailsVO.setOrderDetailList(orderDetailPOList);
        orderDetailsVO.setOrderDishes(orderDishes);
        return orderDetailsVO;
    }

    @Override
    public void delivery(Long id) {
        // 修改订单状态为派送中
        updateById(OrderPO.builder().status(OrderStatusConstant.DELEVERY_PROGRESS).id(Long.valueOf(id)).build());
    }

    @Override
    public PageResult<OrderDetailsVO> conditionSearch(Integer page, Integer pageSize, OrderSearchDTO orderConditions) {
        // 获取分页数据
        Page<OrderPO> pageRecords = new Page<>(page,pageSize);
        lambdaQuery()
                .eq(OrderPO::getStatus,orderConditions.getStatus())
                .like(OrderPO::getNumber,orderConditions.getNumber())
                .like(OrderPO::getPhone,orderConditions.getPhone())
                .ge(OrderPO::getOrderTime,orderConditions.getBeginTime())
                .le(OrderPO::getOrderTime,orderConditions.getEndTime())
                .page(pageRecords);
        List<OrderPO> orderPOList = pageRecords.getRecords();
        // 把OrderPO封装成OrderDetailsVO
        List<OrderDetailsVO> orderDetailsVOList = orderPOList.stream().map(orderPO->{
            OrderDetailsVO orderDetailsVO = BeanUtil.copyProperties(orderPO, OrderDetailsVO.class);
            List<OrderDetailPO> orderDetailPOList = orderDetailService.list(new LambdaQueryWrapper<OrderDetailPO>().eq(OrderDetailPO::getOrderId, orderPO.getId()));
            // 获取orderDishes
            List<Long> dishIds = orderDetailPOList.stream()
                    .filter(detail -> detail.getDishId() != null)
                    .map(OrderDetailPO::getDishId)
                    .collect(Collectors.toList());
            List<Long> setmealIds = orderDetailPOList.stream()
                    .filter(detail -> detail.getSetmealId() != null)
                    .map(OrderDetailPO::getSetmealId)
                    .collect(Collectors.toList());

// 批量查询，缓存名称（用Map避免重复查询）
            Map<Long, String> dishNameMap = dishService.listByIds(dishIds).stream()
                    .collect(Collectors.toMap(DishPO::getId, DishPO::getName));
            Map<Long, String> setmealNameMap = setmealService.listByIds(setmealIds).stream()
                    .collect(Collectors.toMap(SetmealPO::getId, SetmealPO::getName));

// 流式处理时直接从Map获取名称（无数据库查询）
            String orderDishes = orderDetailPOList.stream()
                    .filter(detail -> detail.getDishId() != null || detail.getSetmealId() != null)
                    .map(detail -> {
                        String name = dishNameMap.getOrDefault(detail.getDishId(), setmealNameMap.getOrDefault(detail.getSetmealId(), "未知商品"));
//                    Integer number = detail.getNumber() == null ? 0 : detail.getNumber();
                        return detail.getNumber() == null ? "" :name + "*" + detail.getNumber();
                    })
                    .collect(Collectors.joining(","));
            orderDetailsVO.setOrderDishes(orderDishes);
            return orderDetailsVO;
        }).collect(Collectors.toList());
        return new PageResult<>(pageRecords.getTotal(), orderDetailsVOList);
    }

    @Override
    public ReportStatisticTop top10(LocalDate begin, LocalDate end) {
        // 1. 转换时间范围（当天最小时间~当天最大时间）
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 2. 调用Mapper一次性获取Top10统计结果（SQL已完成联表、分组、排序）
        List<ProductSalesDTO> top10List = orderMapper.selectTop10Sales(
                beginTime,
                endTime,
                OrderStatusConstant.COMPLETED
        );

        // 3. 封装成返回格式（拼接名称和销量字符串）
        String dishNames = top10List.stream()
                .map(ProductSalesDTO::getProductName)
                .collect(Collectors.joining(","));

        String dishNumbers = top10List.stream()
                .map(dto -> dto.getTotalSales().toString())
                .collect(Collectors.joining(","));

        return new ReportStatisticTop(dishNames, dishNumbers);
    }

/*
订单统计
下面这个方法比上面的复杂：复杂条件直接在sql中完成，而不是在service中多次访问数据库，要避免多次访问数据库
 */
//    @Override
    public ReportStatisticTop Complextop10(LocalDate begin, LocalDate end) {
        // 同一日期格式
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        // 找到相应的订单  -- 筛选-分组-排序-查询
        LambdaQueryWrapper<OrderPO> queryWrapper = new LambdaQueryWrapper<OrderPO>()
                .ge(OrderPO::getOrderTime, beginTime)
                .le(OrderPO::getOrderTime, endTime)
                .eq(OrderPO::getStatus, OrderStatusConstant.COMPLETED);
        List<OrderPO> orderList = list(queryWrapper);
        Map<String, Integer> dishMap = new HashMap<>();
        orderList.forEach(orderPO->{
            List<OrderDetailPO> orderDetailList = orderDetailService.list(new LambdaQueryWrapper<OrderDetailPO>().eq(OrderDetailPO::getOrderId, orderPO.getId()));
            // 先把dish和setmeal的名字拿出来
            List<Long> dishIds = orderDetailList.stream().map(OrderDetailPO::getDishId).collect(Collectors.toList());
            List<Long> setmealIds = orderDetailList.stream().map(OrderDetailPO::getSetmealId).collect(Collectors.toList());
            Map<Long,String> dishNamesMap = dishService.listByIds(dishIds).stream().collect(Collectors.toMap(DishPO::getId,DishPO::getName));
            Map<Long,String> setmealNamesMap = setmealService.listByIds(setmealIds).stream().collect(Collectors.toMap(SetmealPO::getId,SetmealPO::getName));
            orderDetailList.forEach(orderDetailPO->{
                // dish
                dishMap.put(dishNamesMap.get(orderDetailPO.getDishId()),dishMap.getOrDefault(dishNamesMap.get(orderDetailPO.getDishId()),0)+orderDetailPO.getNumber());
                // setmeal
                dishMap.put(setmealNamesMap.get(orderDetailPO.getSetmealId()),dishMap.getOrDefault(setmealNamesMap.get(orderDetailPO.getSetmealId()),0)+orderDetailPO.getNumber());
            });
        });
        // 封装
        String dishNames = dishMap.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue()-o1.getValue())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(","));
        String dishNumbers = dishMap.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue()-o1.getValue())
                .limit(10)
                .map(e->e.getValue().toString())
                .collect(Collectors.joining(","));
        return new ReportStatisticTop(dishNames,dishNumbers);
    }

}
