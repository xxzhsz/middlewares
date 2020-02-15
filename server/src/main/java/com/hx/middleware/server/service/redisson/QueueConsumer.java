package com.hx.middleware.server.service.redisson;

import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-15 21:21
 * @description
 */
@Component
public class QueueConsumer implements ApplicationRunner, Ordered {
    private static final Logger log = LoggerFactory.getLogger(QueueConsumer.class);
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        try {
            final String queueName = "RedissonBasicQueue";
            RQueue<String> queue = redissonClient.getQueue(queueName);
            while (true) {
                String message = queue.poll();
                if (message != null) {
                    log.info("消费者队列消费消息:{}", message);
                }
            }
        } catch (Exception e) {
            log.error("消费者队列消费消息出现异常", e.fillInStackTrace());
        }
    }

    /**
     * 返回-1表示在项目启动后启动   表示的是启动顺序
     *
     * @return
     */
    @Override
    public int getOrder() {
        return -1;
    }
}
