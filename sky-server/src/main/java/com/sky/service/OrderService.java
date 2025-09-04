package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.CancelOrderDTO;
import com.sky.dto.OrderSearchDTO;
import com.sky.dto.RejectOrderDTO;
import com.sky.entity.OrderDetailPO;
import com.sky.entity.OrderPO;
import com.sky.result.PageResult;
import com.sky.vo.OrderDetailsVO;
import com.sky.vo.OrderStatisticVO;
import com.sky.vo.ReportStatisticTop;

import java.time.LocalDate;

public interface OrderService extends IService<OrderPO> {
    OrderStatisticVO statistics();

    void cancelOrder(CancelOrderDTO cancelOrderDTO);

    void complete(Long id);

    void rejection(RejectOrderDTO rejectOrderDTO);

    void confirm(Integer id);

    OrderDetailsVO details(Long id);

    void delivery(Long id);

    PageResult<OrderDetailsVO> conditionSearch(Integer page, Integer pageSize, OrderSearchDTO orderConditions);

    ReportStatisticTop top10(LocalDate begin, LocalDate end);
}
