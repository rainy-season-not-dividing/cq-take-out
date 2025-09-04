package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.ReportStatisticTop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Slf4j
@RequestMapping("/admin/report")
public class DataStatisticController {
    @Autowired
    private OrderService orderService;

    @GetMapping("top10")
    public Result<ReportStatisticTop> top10(LocalDate begin, LocalDate end){
        log.info("查询{}~{}的销量排名", begin, end);
        return Result.success(orderService.top10(begin, end));
    }

}
