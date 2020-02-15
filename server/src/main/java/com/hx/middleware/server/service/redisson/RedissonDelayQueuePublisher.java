package com.hx.middleware.server.service.redisson;

import com.hx.middleware.server.rabbitmq.entity.DeadDto;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author jxlgcmh
 * @date 2020-02-15 22:59
 * @description
 */
@Component
public class RedissonDelayQueuePublisher {

    private static final Logger log = LoggerFactory.getLogger(RedissonDelayQueuePublisher.class);
    @Autowired
    private RedissonClient redissonClient;

    /**
     *
     * @param deadDto
     * @param ttl
     */
    public void sendMsg(final DeadDto deadDto, final Long ttl) {
        try {
            final String delayQueueName ="RedissonDelayQueueName";
            // 阻塞式队列实例
            RBlockingDeque<DeadDto> blockingDeque = redissonClient.getBlockingDeque(delayQueueName);
            // 阻塞式队列实例  获取延迟队列实例
            RDelayedQueue<DeadDto> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
            //
            delayedQueue.offer(deadDto,ttl, TimeUnit.MILLISECONDS);
            log.info("redisson延迟队列发送消息:{}",deadDto);
        } catch (Exception e) {
            log.error("redisson延迟队列发送消息出现异常:{}",deadDto,e.fillInStackTrace());
            throw e;
        }

    }

}
