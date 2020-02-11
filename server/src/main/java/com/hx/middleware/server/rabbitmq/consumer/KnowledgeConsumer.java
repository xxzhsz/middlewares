package com.hx.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.server.rabbitmq.entity.KnowledgeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @author jxlgcmh
 * @date 2020-02-11 13:28
 * @description
 */
@Component
public class KnowledgeConsumer {
    private static final Logger log = LoggerFactory.getLogger(KnowledgeConsumer.class);
    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "${mq.auto.knowledge.queue.name}", containerFactory = "listenerContainerAutoFactory")
    public void consumeAutoMsg(@Payload byte[] msg) {
        try {
            KnowledgeInfo info = objectMapper.readValue(msg, KnowledgeInfo.class);
            log.info("基于Auto机制收到的消息为：{}", info);
        } catch (IOException e) {
            log.error("基于Auto机制接收下次出现异常", e.fillInStackTrace());
        }
    }
}
