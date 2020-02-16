package com.hx.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.model.dto.UserLoginDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-11 20:44
 * @description
 */
@Component
public class LoginPublisher {
    private static final Logger log = LoggerFactory.getLogger(LoginPublisher.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;

    public void sendLoginMsg(UserLoginDto userLoginDto) {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange(env.getProperty("mq.login.exchange.name"));
        rabbitTemplate.setRoutingKey(env.getProperty("mq.login.routing.key.name"));
        rabbitTemplate.convertAndSend(userLoginDto, message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,UserLoginDto.class);
            return message;
        });
        log.info("记录登录日志：{}", userLoginDto);
    }


}
