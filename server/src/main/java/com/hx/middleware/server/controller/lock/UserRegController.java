package com.hx.middleware.server.controller.lock;

import com.google.common.base.Strings;
import com.hx.middleware.api.enums.StatusCode;
import com.hx.middleware.api.response.BaseResponse;
import com.hx.middleware.model.dto.UserRegDto;
import com.hx.middleware.server.service.lock.UserRegService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jxlgcmh
 * @date 2020-02-13 16:45
 * @description
 */
@RestController
@RequestMapping("user/reg")
public class UserRegController {
    private static final Logger log = LoggerFactory.getLogger(UserRegController.class);

    @Autowired
    private UserRegService userRegService;

    @RequestMapping(value = "/submit", method = RequestMethod.GET)
    public BaseResponse reg(UserRegDto dto) {
        if (Strings.isNullOrEmpty(dto.getUserName()) || Strings.isNullOrEmpty(dto.getPassword())) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
          //  userRegService.regWithZkDistributeLock(dto);
            userRegService.RegWithRedissonDistributeLock(dto);
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }
}
