package com.sky.config;

import com.sky.constant.RabbitConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectRabbitConfig {
    // todo:这个配置类如何工作的，配合rabbitTemplate

    //队列 起名：DirectQueue
    @Bean
    public Queue DirectQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        // return new Queue("DirectQueue",true,true,false);

        //一般设置一下队列的持久化就好,其余两个就是默认false

        // new Queue(队列名, 是否持久化, 是否排他, 是否自动删除)
//        return new Queue("DirectQueue",true);
        // 未配置死信队列
//        return new Queue(RabbitConstant.QUEUE_ORDER,true);
        // 配置死信队列
        return QueueBuilder.durable(RabbitConstant.QUEUE_ORDER)
                 // “x-dead-letter-exchange” 是 RabbitMQ 预定义的 “死信交换机属性名”，用于指定 当前队列的消息变成 “死信” 后，要转发到哪个交换机（这个交换机就是 “死信交换机”）。
                .withArgument("x-dead-letter-exchange",RabbitConstant.EXCHANGE_ORDER_DEAD)
                .withArgument("x-dead-letter-routing-key",RabbitConstant.ROUTING_KEY_ORDER_DEAD)
                // 消息超时时间，如果超时也进入死信队列
                .withArgument("x-message-ttl",60000)
                .build();
    }

    //Direct交换机 起名：DirectExchange
    @Bean
    public DirectExchange DirectExchange() {
        // new DirectExchange(交换机名, 是否持久化, 是否自动删除)
//        return new DirectExchange("DirectExchange",true,false);
        return new DirectExchange(RabbitConstant.EXCHANGE_ORDER,true,false);
    }

    //绑定
    //将队列和交换机绑定, 并设置用于匹配键：DirectRouting
    @Bean
    public Binding bindingDirect() {
        // 表示， 当 DirectExchange 交换机收到路由键是 “DirectRouting” 的消息时，会转发给 DirectQueue 队列。
        // 生产者发送消息（交换机：DirectExchange，路由键：DirectRouting） → 交换机转发 → 队列：DirectQueue → 消费者从队列获取消息。
        return BindingBuilder.bind(DirectQueue())
                .to(DirectExchange())
                .with(RabbitConstant.ROUTING_KEY_ORDER);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitConstant.EXCHANGE_ORDER_DEAD,true,false);
    }

    @Bean
    public Queue deadLetterQueue(){

        return QueueBuilder.durable(RabbitConstant.QUEUE_ORDER_DEAD)
                .withArgument("x-max-retry-count", 3)
                .build();
    }

    @Bean
    public Binding deadLetterBinding(){
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(RabbitConstant.ROUTING_KEY_ORDER_DEAD);
    }
}
