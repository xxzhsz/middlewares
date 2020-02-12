package com.hx.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-12 09:27
 * @description
 */
@Component
public class DeadOrderPublisher {

     private static final Logger log = LoggerFactory.getLogger(DeadOrderPublisher.class);

     @Autowired
     private RabbitTemplate rabbitTemplate;
     @Autowired
     private ObjectMapper objectMapper;
     @Autowired
    private Environment env;

    public void sendMsg(Integer orderId) {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange(env.getProperty("mq.order.producer.basic.exchange.name"));
        rabbitTemplate.setRoutingKey(env.getProperty("mq.order.producer.basic.routing.key.name"));
        rabbitTemplate.convertAndSend(orderId, message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,Integer.class);
            return message;
        });
        log.info("发送用户下单ID：{}进入死信队列",orderId);
    }



}
