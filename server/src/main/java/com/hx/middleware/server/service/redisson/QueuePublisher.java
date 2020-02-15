package com.hx.middleware.server.service.redisson;

import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-15 21:16
 * @description
 */
@Component
public class QueuePublisher {
     private static final Logger log = LoggerFactory.getLogger(QueuePublisher.class);
     @Autowired
    private RedissonClient redissonClient;

    public void sendBasicMsg(String message) {
        try {
            final String queueName= "RedissonBasicQueue";
            RQueue<String> queue = redissonClient.getQueue(queueName);
            // 往队列中添加消息
            queue.add(message);
            log.info("RedissonBasicQueue生产者队列发送消息:{}",message);
        } catch (Exception e) {
            log.error("RedissonBasicQueue生产者队列发送消息出现异常:{}",message,e.fillInStackTrace());
        }

    }
}
