package com.hx.middleware.server.rabbitmq.consumer;

import com.hx.middleware.server.rabbitmq.entity.DeadDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-15 22:26
 * @description
 */
@Component
public class MqDelayQueueConsumer {
    private static final Logger log = LoggerFactory.getLogger(MqDelayQueueConsumer.class);

    @Autowired
    private Environment env;

    @RabbitListener(queues = "${mq.redisson.dead.real.queue.name}", containerFactory = "singleListenerContainer")
    public void consumeMsg(@Payload DeadDto deadDto) {
        try {
            log.info("死信队列的缺陷测试收到消息:{}", deadDto);
        } catch (Exception e) {
            log.error("死信队列的缺陷测试接收消息出现异常", e.fillInStackTrace());
        }
    }
}
