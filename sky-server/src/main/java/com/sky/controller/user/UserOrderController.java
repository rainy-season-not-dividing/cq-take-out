package com.sky.controller.user;


import com.sky.dto.OrderPaymentDTO;
import com.sky.dto.UserOrderDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.UserOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/user/order")
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("submit")
    public Result<UserOrderVO> submit(@RequestBody UserOrderDTO userOrderDTO){
        log.info("用户下单:{}", userOrderDTO);
        return Result.success(orderService.submitOrder(userOrderDTO));
    }

    @PutMapping("payment")
    public Result<String> payment(@RequestBody OrderPaymentDTO orderPaymentDTO){
        log.info("用户支付:{}", orderPaymentDTO);
        return orderService.payment(orderPaymentDTO);
    }

}
