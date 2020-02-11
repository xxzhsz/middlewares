package com.hx.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.server.entity.EventInfo;
import com.hx.middleware.server.rabbitmq.entity.KnowledgeInfo;
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
 * @date 2020-02-11 14:39
 * @description
 */
@Component
public class KnowledgeManualPublisher {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeManualPublisher.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;

    public void sendManualMsg(KnowledgeInfo info) {
        if (info != null) {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("mq.manual.knowledge.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("mq.manual.knowledge.routing.key.name"));
            try {
                Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(info)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
                rabbitTemplate.convertAndSend(message);
                log.info("基于manual机制发送消息：{}",info);
            } catch (JsonProcessingException e) {
                log.error("基于manual机制发送消息出现异常",e.fillInStackTrace());
            }

        }
    }
}
