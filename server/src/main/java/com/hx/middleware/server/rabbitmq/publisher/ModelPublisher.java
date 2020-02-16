package com.hx.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.hx.middleware.model.entity.EventInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author jxlgcmh
 * @date 2020-02-09 20:19
 * @description
 */
@Component
public class ModelPublisher {

    public static final Logger log = LoggerFactory.getLogger(ModelPublisher.class);
    // 注入序列化和反序列化
    @Autowired
    private ObjectMapper objectMapper;
    // 注入rabbitMq模板
    @Autowired
    private RabbitTemplate rabbitTemplate;
    // 查找环境变量
    @Autowired
    private Environment env;

    /**
     * fanout广播模式发送消息
     *
     * @param info
     */
    public void sendMsg(EventInfo info) {
        if (info != null) {
            // 设置传输格式
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            // 设置交换机
            rabbitTemplate.setExchange(env.getProperty("mq.fanout.exchange.name"));
            try {
                Message msg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(info)).build();
                rabbitTemplate.convertAndSend(msg);
                // log
                log.info("fanout广播模式发送消息:{}", info);
            } catch (JsonProcessingException e) {
                log.error("fanout广播模式发送消息出现异常：{}", info, e.fillInStackTrace());
            }
        }
    }

    public void sendMsgByDirectOne(EventInfo info) {
        if (info != null) {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            // 设置交换机
            rabbitTemplate.setExchange(env.getProperty("mq.direct.exchange.name"));
            // 设置路由键
            rabbitTemplate.setRoutingKey(env.getProperty("mq.direct.routing.key.name.one"));
            try {
                Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(info)).build();
                rabbitTemplate.convertAndSend(message);
                log.info("direct-one广播模式发送消息:{}", info);
            } catch (JsonProcessingException e) {
                log.error("direct-one广播模式发送消息出现异常：{}", info, e.fillInStackTrace());
            }
        }
    }

    public void sendMsgByDirectTwo(EventInfo info) {
        if (info != null) {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            // 设置交换机
            rabbitTemplate.setExchange(env.getProperty("mq.direct.exchange.name"));
            // 设置路由键
            rabbitTemplate.setRoutingKey(env.getProperty("mq.direct.routing.key.name.two"));
            try {
                Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(info)).build();
                rabbitTemplate.convertAndSend(message);
                log.info("direct-one广播模式发送消息:{}", info);
            } catch (JsonProcessingException e) {
                log.error("direct-two广播模式发送消息出现异常：{}", info, e.fillInStackTrace());
            }
        }
    }

    public void sendMsgByTopic(String msg, String routingKey) {
        if (!Strings.isNullOrEmpty(msg) && !Strings.isNullOrEmpty(routingKey)) {

            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            // 设置交换机
            rabbitTemplate.setExchange(env.getProperty("mq.topic.exchange.name"));
            // 设置路由键
            rabbitTemplate.setRoutingKey(routingKey);
            Message message = MessageBuilder.withBody(msg.getBytes(StandardCharsets.UTF_8)).build();
            rabbitTemplate.convertAndSend(message);
            log.info("topic模式发送消息:{},routingKey:{}", msg, routingKey);
        }
    }


}
