package com.sky.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RabbitSerializerConfig {

    // 1. 先定义自定义的 ObjectMapper（配置序列化规则，如支持 LocalDateTime）
//    @Bean
//    public ObjectMapper rabbitObjectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        // 支持 JSR310 时间类型（LocalDateTime、LocalDate 等）
//        objectMapper.registerModule(new JavaTimeModule());
//        // 禁用日期时间戳格式（改为字符串格式，如 "2025-09-06T12:30:00"）
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        // 允许序列化私有字段（确保对象的所有字段都能被序列化）
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//
//        return objectMapper;
//    }

    // 2. 直接先自定义jackson2序列化转换器，配置好//应为后续rabbit的消费者需要用到
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());          // <-- 关键
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return new Jackson2JsonMessageConverter(mapper);
    }

    // 2. 定义 RabbitTemplate，通过构造函数传入 Jackson2JsonMessageConverter（含自定义 ObjectMapper）
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // 关键：通过构造函数传入自定义的 ObjectMapper，替代 setObjectMapper()
//        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter(rabbitObjectMapper);

        // 设置 RabbitTemplate 的消息转换器（发送和接收都用 JSON 序列化）
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);

        // 可选：配置生产者确认机制（确保消息发送成功）
//        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//            if (ack) {
//                log.info("消息发送成功，correlationId: {}", correlationData != null ? correlationData.getId() : "无");
//            } else {
//                log.error("消息发送失败，原因: {}", cause);
//            }
//        });

        // 可选：配置消息返回机制（当消息无法路由到队列时回调）
//        rabbitTemplate.setReturnsCallback(returned -> {
//            log.error("消息路由失败，exchange: {}, routingKey: {}, message: {}",
//                    returned.getExchange(), returned.getRoutingKey(), returned.getMessage());
//        });

        return rabbitTemplate;
    }
}
