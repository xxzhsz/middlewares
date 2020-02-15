package com.hx.middleware.server.service.redisson;

import com.hx.middleware.server.dto.UserLoginDto;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-15 12:11
 * @description
 */
@Component
public class UserLoginPublisher {
    private static final Logger log = LoggerFactory.getLogger(UserLoginPublisher.class);

    private static final String TOPIC_KEY = "redissonUserLoginTopicKey";

    @Autowired
    private RedissonClient redissonClient;

    public void sendMsg(UserLoginDto dto) {
        try {
            if (dto != null) {
                RTopic topic = redissonClient.getTopic(TOPIC_KEY);
                topic.publishAsync(dto);
                // 异步发送消息
                log.info("redisson记录登录日志:{}", dto);
            }
        } catch (Exception e) {
            log.error("redisson发送登录日志出现异常:{}",e.fillInStackTrace());
        }
    }
}
