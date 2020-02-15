package com.hx.middleware.server.service.redisson;

import com.hx.middleware.server.rabbitmq.entity.DeadDto;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-15 23:09
 * @description
 */
@Component
@EnableScheduling
public class RedissonDelayQueueConsumer {
    private static final Logger log = LoggerFactory.getLogger(RedissonDelayQueueConsumer.class);

    @Autowired
    private RedissonClient redissonClient;

    @Scheduled(cron = "*/1 * * * * ?")
    public void consumeMsg() {
        try {
            final String delayQueueName = "RedissonDelayQueueName";
            RBlockingDeque<DeadDto> blockingDeque = redissonClient.getBlockingDeque(delayQueueName);
            DeadDto deadDto = blockingDeque.take();
            log.info("redisson延迟队列收到消息为:{}", deadDto);
        } catch (Exception e) {
            log.error("redisson延迟队列接收消息为出现异常", e.fillInStackTrace());
        }
    }

}
