package com.hx.middleware.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.server.entity.EventInfo;
import com.hx.middleware.server.rabbitmq.entity.Person;
import com.hx.middleware.server.rabbitmq.publisher.BasicPublisher;
import com.hx.middleware.server.rabbitmq.publisher.ModelPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jxlgcmh
 * @date 2020-02-10 08:29
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RabbitMqTest {
    private static final Logger log = LoggerFactory.getLogger(RabbitMqTest.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BasicPublisher publisher;
    @Autowired
    private ModelPublisher modelPublisher;

    /**
     * 测试基本消息模式发送消息-字符串
     */
    @Test
    public void one() {
        String msg = "hello";
        publisher.sendMsg(msg);
    }

    /**
     * 测试基本消息模式发送消息对象
     */
    @Test
    public void two() {
        Person person = new Person(1, "chen", "minghua");
        publisher.sendObjectMsg(person);
    }

    /**
     * 测试fanout
     */
    @Test
    public void three() {
        EventInfo info = new EventInfo(1, "A", "B", "c");
        modelPublisher.sendMsg(info);
        log.info("用户发送消息");
    }

    /**
     * 测试direct
     */
    @Test
    public void four() {
        EventInfo info1 = new EventInfo(1, "A", "B", "c");
        EventInfo info2 = new EventInfo(2, "c", "d", "e");
        modelPublisher.sendMsgByDirectOne(info1);
        modelPublisher.sendMsgByDirectTwo(info2);
    }

    /**
     * 测试topic
     */
    @Test
    public void five() {
        String message = "topic模式的消息";
        // 这个消息会发送到#和*
        String routingKeyOne = "local.middleware.mq.topic.java.routing.key";
        // 会发送到#
        String routingKeyTwo = "local.middleware.mq.topic.c.c++.routing.key";
        // 会发送到#  并且相当于没有单词
        String routingKeyThree = "local.middleware.mq.topic.routing.key";
        //test1
        //  modelPublisher.sendMsgByTopic(message,routingKeyOne);
        //test2
        //  modelPublisher.sendMsgByTopic(message,routingKeyTwo);
        // test3
        modelPublisher.sendMsgByTopic(message, routingKeyThree);
    }

}
