package com.hx.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.server.rabbitmq.entity.DeadDto;
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
 * @date 2020-02-15 22:12
 * @description
 */
@Component
public class MqDelayQueuePublisher {
    private static final Logger log = LoggerFactory.getLogger(MqDelayQueuePublisher.class);

    @Autowired
    private Environment env;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @param deadDto 消息传输对象
     * @param ttl     延迟时间
     */
    public void sendMsg(final DeadDto deadDto, final Long ttl) {
        try {
            // 摸板配置返送的基本交换机和基本路由
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("mq.redisson.dead.basic.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("mq.redisson.dead.basic.routing.key.name"));
            rabbitTemplate.convertAndSend(deadDto, message -> {
                MessageProperties properties = message.getMessageProperties();
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, DeadDto.class);
                /**
                 * 重要的,在此处设置消息过期时间
                 */
                properties.setExpiration(String.valueOf(ttl));
                return message;
            });
            log.info("死信队列的缺陷测试发送消息正常:{}", deadDto);
        } catch (Exception ignored) {
            log.error("死信队列的缺陷测试发送消息出现异常:{}", deadDto, ignored.fillInStackTrace());
            throw ignored;
        }
    }


}
