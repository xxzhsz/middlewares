package com.hx.middleware.server;

import com.hx.middleware.server.event.Publisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jxlgcmh
 * @date 2020-02-08 10:16
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class EventTest {
    private static final Logger log = LoggerFactory.getLogger(EventTest.class);

    @Autowired
    private Publisher publisher;

    @Test
    public void sendMsg() {
        publisher.sendMsg();
    }
}
