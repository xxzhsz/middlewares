package com.hx.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.model.entity.EventInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author jxlgcmh
 * @date 2020-02-09 20:35
 * @description
 */
@Component
public class ModelConsumer {
    public static final Logger log = LoggerFactory.getLogger(ModelConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;


    @RabbitListener(queues = "${mq.fanout.queue.name.one}", containerFactory = "singleListenerContainer")
    public void consumerFanoutMsgOne(@Payload byte[] msg) {
        try {
            EventInfo eventInfo = objectMapper.readValue(msg, EventInfo.class);
            log.info("fanout消息模型队列one消费到消息：{}", eventInfo);
        } catch (IOException e) {
            log.info("fanout消息模型队列one消费消息出现异常：", e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "${mq.fanout.queue.name.two}", containerFactory = "singleListenerContainer")
    public void consumerFanoutMsgTwo(@Payload byte[] msg) {
        try {
            EventInfo eventInfo = objectMapper.readValue(msg, EventInfo.class);
            log.info("fanout消息模型队列two消费到消息：{}", eventInfo);
        } catch (IOException e) {
            log.info("fanout消息模型队列two消费消息出现异常：", e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "${mq.direct.queue.name.one}", containerFactory = "singleListenerContainer")
    public void consumerDirectMsgOne(@Payload byte[] msg) {
        try {
            EventInfo eventInfo = objectMapper.readValue(msg, EventInfo.class);
            log.info("direct-one消息队列消费到消息：{}", eventInfo);
        } catch (IOException e) {
            log.info("direct-one消息队列消费消息出现异常：", e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "${mq.direct.queue.name.two}", containerFactory = "singleListenerContainer")
    public void consumerDirectMsgTwo(@Payload byte[] msg) {
        try {
            EventInfo eventInfo = objectMapper.readValue(msg, EventInfo.class);
            log.info("direct-two消息队列消费到消息：{}", eventInfo);
        } catch (IOException e) {
            log.info("direct-two消息队列消费消息出现异常：", e.fillInStackTrace());
            e.printStackTrace();
        }
    }


    @RabbitListener(queues = "${mq.topic.queue.name.one}", containerFactory = "singleListenerContainer")
    public void consumerTopicMsgOne(@Payload byte[] msg) {
        String message = new String(msg, StandardCharsets.UTF_8);
        log.info("topic-*消息队列消费到消息：{}", message);
    }

    @RabbitListener(queues = "${mq.topic.queue.name.two}", containerFactory = "singleListenerContainer")
    public void consumerTopicMsgTwo(@Payload byte[] msg) {
        String message = new String(msg, StandardCharsets.UTF_8);
        log.info("topic-#消息队列消费到消息：{}", message);
    }


}
