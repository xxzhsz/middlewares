package com.hx.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.server.rabbitmq.entity.DeadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-11 22:07
 * @description
 */
@Component
public class DeadConsumer {
     private static final Logger log = LoggerFactory.getLogger(DeadConsumer.class);

     @Autowired
     private ObjectMapper objectMapper;
    @RabbitListener(queues = "${mq.consumer.queue.name}",containerFactory = "singleListenerContainer")
    public void consumerMsg(@Payload DeadInfo info) {
        log.info("真正队列监听到消息：{}",info);
    }
}
