package com.hx.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.hx.middleware.model.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author jxlgcmh
 * @date 2020-02-10 08:52
 * @description
 */
@Component
public class BasicPublisher {
    private static final Logger log = LoggerFactory.getLogger(BasicPublisher.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;

    public void sendMsg(String msg) {
        if (!Strings.isNullOrEmpty(msg)) {
            //设置消息形式
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            // 设置消息的交换机
            rabbitTemplate.setExchange(env.getProperty("mq.basic.info.exchange.name"));
            //设置路由键
            rabbitTemplate.setRoutingKey(env.getProperty("mq.basic.info.routing.key.name"));
            Message message = MessageBuilder.withBody(msg.getBytes(StandardCharsets.UTF_8)).build();
            rabbitTemplate.convertAndSend(message);
            log.info("基本消费模型====生产者发送消息：{}", msg);

        }
    }

    /**
     * 消息的传输对象是对象
     * @param person
     */
    public void sendObjectMsg(Person person) {
        if (person != null){
            //设置消息形式
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            // 设置消息的交换机
            rabbitTemplate.setExchange(env.getProperty("mq.object.info.exchange.name"));
            //设置路由键
            rabbitTemplate.setRoutingKey(env.getProperty("mq.object.info.routing.key.name"));
            rabbitTemplate.convertAndSend(person, message -> {
                MessageProperties properties = message.getMessageProperties();
                // 配置属性
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, Person.class);
                return message;
            });
            log.info("基本消费模型====生产者发送消息对象：{}", person);

        }
    }

}
