package com.hx.middleware.server.service;

import com.hx.middleware.model.entity.User;
import com.hx.middleware.model.mapper.UserMapper;
import com.hx.middleware.server.dto.UserLoginDto;
import com.hx.middleware.server.rabbitmq.publisher.LoginPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jxlgcmh
 * @date 2020-02-11 20:26
 * @description
 */
@Service
public class UserService {
     private static final Logger log = LoggerFactory.getLogger(UserService.class);

     @Autowired
     private UserMapper userMapper;
     @Autowired
     private LoginPublisher loginPublisher;


    public Boolean login(UserLoginDto userLoginDto) {
        User user = userMapper.selectByUserNamePassword(userLoginDto.getUserName(), userLoginDto.getPassword());
        if (user != null) {
            userLoginDto.setUserId(user.getId());
            //发送消息登录成功
            loginPublisher.sendLoginMsg(userLoginDto);
            log.info("发送登录日志！");
            return true;
        }else {
            return false;
        }
    }
}
