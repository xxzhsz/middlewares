package com.hx.middleware.server.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-08 10:07
 * @description
 */
@Component
@EnableAsync
public class Consumer implements ApplicationListener<LoginEvent> {
    private static  final  Logger log = LoggerFactory.getLogger(Consumer.class);

    @Override
    @Async
    public void onApplicationEvent(LoginEvent loginEvent) {
        log.info("spring事件模型监听到事件：{}", loginEvent);
    }
}
