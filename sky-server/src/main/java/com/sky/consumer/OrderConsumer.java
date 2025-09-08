package com.sky.consumer;


import com.rabbitmq.client.Channel;
import com.sky.constant.RabbitConstant;
import com.sky.entity.OrderPO;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 订单消费者
 */
@Component
@Slf4j
public class OrderConsumer {


    @Autowired
    private OrderService orderService;

    // ExecutorService满足日常需求，ThreadPoolExecutor更适合高并发场景
//    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    @RabbitListener(
            queues = RabbitConstant.QUEUE_ORDER,
            messageConverter = "jackson2JsonMessageConverter",
            concurrency="20")
    @Transactional(rollbackFor = Exception.class)   //这个注解的意思是：如果方法中出现异常，则进行回滚
    public void consume(OrderPO orderPO, Channel channel, Message msg) throws IOException {
        long deleveryTag = msg.getMessageProperties().getDeliveryTag();

        try {
            // todo： 通过springwebsock ，给服务器发送信息，通知新的订单已生成
            // 写入db， 异步操作
            orderService.save(orderPO);
            // 手动确认消息（配置中设置的），mutiple=false，表示只确认当前消息
            channel.basicAck(deleveryTag, false);
        } catch(Exception e){
            log.error("订单生成失败：{}", e.getMessage());
            // 手动拒绝消息并重回队列，requeue=true 表示重试，重回本队列，false表示进入死信队列，不会本队列
            channel.basicNack(deleveryTag, false, false);
        }
        log.info("新的订单已生成，id为：{}", orderPO.getId());

    }
}
