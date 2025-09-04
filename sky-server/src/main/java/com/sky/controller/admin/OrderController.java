package com.sky.controller.admin;


import com.sky.dto.CancelOrderDTO;
import com.sky.dto.OrderSearchDTO;
import com.sky.dto.RejectOrderDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderDetailsVO;
import com.sky.vo.OrderStatisticVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("statistics")
    public Result<OrderStatisticVO> statistics(){
        log.info("订单统计");
        return Result.success(orderService.statistics());
    }

    @PutMapping("cancel")
    public Result cancelOrder(@RequestBody CancelOrderDTO cancelOrderDTO){
        log.info("取消订单：{}",cancelOrderDTO);
        orderService.cancelOrder(cancelOrderDTO);
        return Result.success();
    }

    @PutMapping("complete/{id}")
    public Result complete(@PathVariable Long id){
        log.info("完成订单：{}",id);
        orderService.complete(id);
        return Result.success();
    }

    @PutMapping("rejection")
    public Result rejection(@RequestBody RejectOrderDTO rejectOrderDTO){
        log.info("拒绝订单：{}",rejectOrderDTO);
        orderService.rejection(rejectOrderDTO);
        return Result.success();
    }

    @PutMapping("confirm")
    public Result confirm(@RequestParam(value = "id") Integer id){
        log.info("确认订单：{}",id);
        orderService.confirm(id);
        return Result.success();
    }

    @GetMapping("details/{id}")
    public Result<OrderDetailsVO> details(@PathVariable Long id){
        log.info("查询订单详情：{}",id);
        return Result.success(orderService.details(id));
    }

    @PutMapping("delivery/{id}")
    public Result delivery(@PathVariable Long id){
        log.info("派送订单：{}",id);
        orderService.delivery(id);
        return Result.success();
    }

    @GetMapping("conditionSearch")
    public Result<PageResult<OrderDetailsVO>> conditionSearch(Integer page, Integer pageSize, OrderSearchDTO orderConditions){
        log.info("分页查询订单：{}",orderConditions);
        return Result.success(orderService.conditionSearch(page,pageSize, orderConditions));
    }



}
