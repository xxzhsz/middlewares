package com.hx.middleware.server.controller.rabbitmq;

import com.hx.middleware.api.enums.StatusCode;
import com.hx.middleware.api.response.BaseResponse;
import com.hx.middleware.model.dto.UserOrderDto;
import com.hx.middleware.server.service.DeadOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jxlgcmh
 * @date 2020-02-12 08:11
 * @description
 */
@RestController
@RequestMapping("user/order")
public class UserOrderController {
    private static final Logger log = LoggerFactory.getLogger(UserOrderController.class);

    @Autowired
    private DeadOrderService deadOrderService;


    @RequestMapping(value = "/push", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse makeOrder(@RequestBody @Validated UserOrderDto userOrderDto, BindingResult result) {
        if (result.hasErrors()) {
            return new BaseResponse(StatusCode.InvalidParams);
        } else {
            deadOrderService.push(userOrderDto);
            return new BaseResponse(StatusCode.Success);
        }
    }
}
