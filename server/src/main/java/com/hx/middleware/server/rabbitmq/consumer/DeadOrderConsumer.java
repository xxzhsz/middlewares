package com.hx.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.model.entity.UserOrder;
import com.hx.middleware.model.mapper.UserOrderMapper;
import com.hx.middleware.server.service.DeadOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author jxlgcmh
 * @date 2020-02-12 09:39
 * @description
 */
@Component
public class DeadOrderConsumer {
    private static final Logger log = LoggerFactory.getLogger(DeadOrderConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserOrderMapper userOrderMapper;
    @Autowired
    private DeadOrderService deadOrderService;

    @RabbitListener(queues = "${mq.order.consumer.queue.name}", containerFactory = "singleListenerContainer")
    public void consumeMsg(@Payload Integer orderId) {
        log.info("监听到死信队列消息orderId：{}", orderId);
        UserOrder userOrder = userOrderMapper.selectByIdAndStatus(orderId, 1);
        if (userOrder != null) {
            // 说明还没有支付
            deadOrderService.updateUserOrderRecord(userOrder);
        } else {
            log.info("已支付");
        }

    }
}
