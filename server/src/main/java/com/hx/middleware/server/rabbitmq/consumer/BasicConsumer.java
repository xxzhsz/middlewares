package com.hx.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.server.rabbitmq.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author jxlgcmh
 * @date 2020-02-10 09:04
 * @description
 */
@Component
public class BasicConsumer {
     private static final Logger log = LoggerFactory.getLogger(BasicConsumer.class);

     @Autowired
     private ObjectMapper objectMapper;

     @RabbitListener(queues="${mq.basic.info.queue.name}",containerFactory = "singleListenerContainer")
    public void consumeMsg(@Payload byte[] msg) {
         String message = new String(msg, StandardCharsets.UTF_8);
         log.info("基本消息模型====消费者消费消息：{}",message);
     }

    @RabbitListener(queues="${mq.object.info.queue.name}",containerFactory = "singleListenerContainer")
    public void consumeMsg(@Payload Person person) {
        log.info("基本消息模型====消费者消费对象型消息：{}",person);
    }

}
