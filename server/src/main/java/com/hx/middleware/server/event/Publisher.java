package com.hx.middleware.server.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jxlgcmh
 * @date 2020-02-08 10:10
 * @description
 */
@Component
public class Publisher {

    private static final Logger log = LoggerFactory.getLogger(Publisher.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void sendMsg() {
        LoginEvent loginEvent = new LoginEvent(this, "debug", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), "127.0.0.1");
        eventPublisher.publishEvent(loginEvent);
        log.info("spring事件驱动模型,触发事件{}",loginEvent);
    }
}
