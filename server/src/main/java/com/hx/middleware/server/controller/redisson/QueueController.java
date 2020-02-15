package com.hx.middleware.server.controller.redisson;

import com.hx.middleware.api.enums.StatusCode;
import com.hx.middleware.api.response.BaseResponse;
import com.hx.middleware.server.service.redisson.QueuePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jxlgcmh
 * @date 2020-02-15 21:29
 * @description
 */
@RestController
@RequestMapping("queue")
public class QueueController {
    private static final Logger log = LoggerFactory.getLogger(QueueController.class);
    @Autowired
    private QueuePublisher queuePublisher;

    @RequestMapping(value = "/basic/msg/send", method = RequestMethod.GET)
    public BaseResponse sendMsg(@RequestParam String message) {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            queuePublisher.sendBasicMsg(message);
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }

}
