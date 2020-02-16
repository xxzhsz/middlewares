package com.hx.middleware.server.controller.rabbitmq;

import com.hx.middleware.api.enums.StatusCode;
import com.hx.middleware.api.response.BaseResponse;
import com.hx.middleware.model.dto.UserLoginDto;
import com.hx.middleware.server.service.UserService;
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
 * @date 2020-02-11 20:22
 * @description
 */
@RestController
@RequestMapping("user")
public class UserController {
     private static final Logger log = LoggerFactory.getLogger(UserController.class);

     @Autowired
     private UserService userService;

     @RequestMapping(value = "/login",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
     public BaseResponse login(@RequestBody @Validated UserLoginDto userLoginDto, BindingResult result) {
         if (result.hasErrors()) {
             return new BaseResponse(StatusCode.InvalidParams);
         }else {
             Boolean flag = userService.login(userLoginDto);
             if (flag) {
                 return new BaseResponse(StatusCode.Success.getCode(),"登录成功");
             }else {
                 return new BaseResponse(StatusCode.Fail.getCode(),"账户名或密码错误");
             }
         }
     }
}
