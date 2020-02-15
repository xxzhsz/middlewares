package com.hx.middleware.server.service.redisson;

import com.hx.middleware.server.dto.UserLoginDto;
import com.hx.middleware.server.service.SysLogService;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-15 12:13
 * @description 实现两个接口
 */
@Component
public class UserLoginSubscriber implements ApplicationRunner, Ordered {
    private static final Logger log = LoggerFactory.getLogger(UserLoginSubscriber.class);
    private static final String TOPIC_KEY = "redissonUserLoginTopicKey";

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private SysLogService sysLogService;

    /**
     * 监听消息
     *
     * @param applicationArguments
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        try {
            RTopic topic = redissonClient.getTopic(TOPIC_KEY);
            topic.addListener(UserLoginDto.class, (charSequence, userLoginDto) -> {
                log.info("Redisson消费者监听到消息:{}", userLoginDto);
                if (userLoginDto != null) {
                    sysLogService.recordLoginLog(userLoginDto);
                }
            });
        } catch (Exception e) {
            log.error("消费者监听消息出现异常:{}", e.fillInStackTrace());
        }
    }

    /**
     * 返回0表示的是启动顺序项目启动也跟着启动
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
