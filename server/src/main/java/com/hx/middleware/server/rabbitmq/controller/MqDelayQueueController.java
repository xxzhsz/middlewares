package com.hx.middleware.server.rabbitmq.controller;

import com.hx.middleware.api.enums.StatusCode;
import com.hx.middleware.api.response.BaseResponse;
import com.hx.middleware.server.rabbitmq.entity.DeadDto;
import com.hx.middleware.server.rabbitmq.publisher.MqDelayQueuePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jxlgcmh
 * @date 2020-02-15 22:32
 * @description
 */
@RestController
@RequestMapping("queue")
public class MqDelayQueueController {
     private static final Logger log = LoggerFactory.getLogger(MqDelayQueueController.class);

    @Autowired
    public MqDelayQueuePublisher mqDelayQueuePublisher;

    @RequestMapping("/basic/msg/delay/send")
    public BaseResponse sendMsg() {
        BaseResponse response =new BaseResponse(StatusCode.Success);
        DeadDto deadDto1 =new DeadDto(1,"lucy");
        Long ttl1 =10000L;
        DeadDto deadDto2 =new DeadDto(2,"mimi");
        Long ttl2 =5000L;
        DeadDto deadDto3 =new DeadDto(3,"jack");
        Long ttl3 =2000L;
        try {
            mqDelayQueuePublisher.sendMsg(deadDto1,ttl1);
            mqDelayQueuePublisher.sendMsg(deadDto2,ttl2);
            mqDelayQueuePublisher.sendMsg(deadDto3,ttl3);
        } catch (Exception e) {
            response =new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}
