package com.hx.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.model.entity.KnowledgeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-11 13:18
 * @description  确认消息机制的消息的发送者
 */
@Component
public class KnowledgePublisher {
     private static final Logger log = LoggerFactory.getLogger(KnowledgePublisher.class);

     @Autowired
     private ObjectMapper objectMapper;
     @Autowired
     private RabbitTemplate rabbitTemplate;
     @Autowired
    private Environment env;


    public void sendAutoMsg(KnowledgeInfo info) {
        if (info != null) {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("mq.auto.knowledge.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("mq.auto.knowledge.routing.key.name"));
            try {
                // etDeliveryMode(MessageDeliveryMode.PERSISTENT)  设置消息持久化模式
                Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(info)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
                rabbitTemplate.convertAndSend(message);
                log.info("基于auto发送消息：{}",info);
            } catch (JsonProcessingException e) {
                log.error("基于auto发送消息出现异常:{}",info,e.fillInStackTrace());
            }

        }
    }
}
