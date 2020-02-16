package com.hx.middleware.server.rabbitmq.consumer;

import com.hx.middleware.model.dto.UserLoginDto;
import com.hx.middleware.server.service.SysLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-11 20:55
 * @description
 */
@Component
public class LoginConsumer {
     private static final Logger log = LoggerFactory.getLogger(LoginConsumer.class);
     @Autowired
     private SysLogService sysLogService;

     @RabbitListener(queues = "${mq.login.queue.name}",containerFactory = "singleListenerContainer")
    public void consumeLoginLog(@Payload UserLoginDto userLoginDto) {
        try {
            log.info("监听到的登录日志信息为：{}",userLoginDto);
            sysLogService.recordLoginLog(userLoginDto);
        } catch (Exception e) {
            log.error("监听登录日志出现异常",e.fillInStackTrace());
        }
    }

}
