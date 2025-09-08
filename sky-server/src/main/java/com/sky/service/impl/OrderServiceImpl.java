package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.OrderStatusConstant;
import com.sky.constant.PayStatusConstant;
import com.sky.constant.RabbitConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.*;
import com.sky.utils.NumberUtil;
import com.sky.vo.OrderDetailsVO;
import com.sky.vo.OrderStatisticVO;
import com.sky.vo.ReportStatisticTop;
import com.sky.vo.UserOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Resource
    private RabbitTemplate rabbitTemplate;

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
        // 修改订单状态: 支付状态、订单状态
        orderPO.setStatus(OrderStatusConstant.CANCELED);
        orderPO.setPayStatus(PayStatusConstant.PAYMENT_REBACK);
        updateById(orderPO);
    }

    @Override
    public void complete(Long id) {
        // 1、修改订单状态--状态修改为完成
        updateById(OrderPO.builder().id(id).status(OrderStatusConstant.COMPLETED).deliveryTime(LocalDateTime.now()).build());
    }

    @Override
    public void rejection(RejectOrderDTO rejectOrderDTO) {
        // 修改订单状态--状态修改为拒绝
        updateById(OrderPO.builder().status(OrderStatusConstant.REJECTED).id(Long.valueOf(rejectOrderDTO.getId())).build());
    }

    @Override
    public void confirm(Integer id) {
        // 修改订单状态--状态修改为接单
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
        LocalDateTime beginTime = null; // 初始化为null
        // 仅当前端传了beginTime（非null），才转换时间
        if (orderConditions.getBeginTime() != null) {
            beginTime = LocalDateTime.of(orderConditions.getBeginTime(), LocalTime.MIN);
        }

        LocalDateTime endTime = null; // 初始化为null
        if (orderConditions.getEndTime() != null) {
            endTime = LocalDateTime.of(orderConditions.getEndTime(), LocalTime.MAX);
        }
        Integer status = orderConditions.getStatus();
        String phone = orderConditions.getPhone();
        String number = orderConditions.getNumber();
        lambdaQuery()
                .eq(status!=null,OrderPO::getStatus,status)
                .like(number!=null,OrderPO::getNumber,number)
                .like(phone!=null,OrderPO::getPhone,phone)
                .ge(beginTime!=null,OrderPO::getOrderTime,beginTime)
                .le(endTime!=null,OrderPO::getOrderTime,endTime)
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

    /*
    订单统计
    下面这个方法比其下面一个的复杂：复杂条件直接在sql中完成，而不是在service中多次访问数据库，要避免多次访问数据库
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

    @Override
    public UserOrderVO submitOrder(UserOrderDTO userOrderDTO) {
        // 获取当前用户
        Long id = BaseContext.getCurrentId();
        log.info("用户下单:{}", id);
        String userName = userService.getById(id).getName();
        OrderPO orderPO = BeanUtil.copyProperties(userOrderDTO, OrderPO.class);
        // 获取当前用户地址
        AddressBookPO address = addressBookService.getById(userOrderDTO.getAddressBookId());
        // 补充其它信息
        orderPO.setUserId(id);
        orderPO.setUserName(userName);
        orderPO.setConsignee(address.getConsignee());
        orderPO.setPhone(address.getPhone());
        orderPO.setAddress(address.getProvinceName()+address.getCityName()+address.getDistrictName()+address.getDetail());
        orderPO.setStatus(OrderStatusConstant.PAYMENT_WAITING);// 订单状态
        orderPO.setPayStatus(PayStatusConstant.PAYMENT_WATING); // 支付状态
        // 生成订单号
        orderPO.setNumber(NumberUtil.generateOrderNumber());
        orderPO.setOrderTime(LocalDateTime.now());
        // 插入订单信息，但是此时仍然是未支付状态---将订单信息插入到redis stream中，/直接改为消息队列，插入到消息队列中
        // 通过线程池异步拉取订单消息并写入数据库
        /**
         * 1、RabbitMQ的工具类：实现写入功能 ：生产者类(本类) 、消费者类 、 配置类（路由）
         * 2、调用生产者类发送信息、消费者类持续拉取信息并进行消费，写入db，线程池-多线程
         */
        rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_ORDER,RabbitConstant.ROUTING_KEY_ORDER,orderPO);
        /**
         * 直接save，不用MQ
         */
        //        save(orderPO);
        UserOrderVO userOrderVO = UserOrderVO.builder()
                // 订单id应该不用返回，只要返回订单号Number即可！！
//                .id(Math.toIntExact(orderPO.getId()))
                .orderAmount(BigDecimal.valueOf(userOrderDTO.getAmount()))
                .orderNumber(orderPO.getNumber())
                .orderTime(orderPO.getOrderTime().toString()).build();
        // 返回数据
        return userOrderVO;
    }

    @Override
    public Result<String> payment(OrderPaymentDTO orderPaymentDTO) {
        // 检查订单是否存在
        OrderPO orderPO = getOne(new LambdaQueryWrapper<>(OrderPO.class).eq(OrderPO::getNumber, orderPaymentDTO.getOrderNumber()));
        if (orderPO == null) {
            return Result.error("订单不存在");
        }
        // 修改订单表信息 ： 支付方式、订单状态(待接单)、支付状态（已支付）、支付时间（now）、预计送达时间（now+establishTime）
        orderPO.setPayMethod(orderPaymentDTO.getPayMethod());
        orderPO.setStatus(OrderStatusConstant.PICK_UP_WAITING);
        orderPO.setPayMethod(orderPaymentDTO.getPayMethod());
        orderPO.setPayStatus(PayStatusConstant.PAYMENT_FINISHED);
        orderPO.setCheckoutTime(LocalDateTime.now());
        orderPO.setEstimatedDeliveryTime(orderPO.getEstimatedDeliveryTime());
        updateById(orderPO);
        String estimatedDeliveryTime = orderPO.getEstimatedDeliveryTime().toString();
        return Result.success(estimatedDeliveryTime);
    }





}





