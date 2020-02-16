package com.hx.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.model.entity.KnowledgeInfo;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author jxlgcmh
 * @date 2020-02-11 14:20
 * @description
 */
@Component("knowledgeManualConsumer")
public class KnowledgeManualConsumer implements ChannelAwareMessageListener {
     private static final Logger log = LoggerFactory.getLogger(KnowledgeManualConsumer.class);

     @Autowired
     private ObjectMapper objectMapper;
    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        MessageProperties properties = message.getMessageProperties();
        long tag = properties.getDeliveryTag();
        byte[] body = message.getBody();
        try {
            KnowledgeInfo info  = objectMapper.readValue(body, KnowledgeInfo.class);
            log.info("手动确认消息：{}",info);
            //tag 消息分发表示   true 是否允许批量消费
            channel.basicAck(tag,true);
        } catch (IOException e) {
            log.error("手动确认消息出现异常",e.fillInStackTrace());
            // 如果出现异常，依然需要手动确认消息，否则该消息将一直留在队列中，从而导致重复消费
            channel.basicReject(tag,false);
        }

    }
}
