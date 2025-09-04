package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.dto.ProductSalesDTO;
import com.sky.entity.OrderPO;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<OrderPO> {

    // 统计指定时间内热销Top10（菜品+套餐）
    List<ProductSalesDTO> selectTop10Sales(
            @Param("beginTime") LocalDateTime beginTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("completedStatus") Integer completedStatus
    );



}
