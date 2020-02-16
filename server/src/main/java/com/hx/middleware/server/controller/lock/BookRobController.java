package com.hx.middleware.server.controller.lock;

import com.google.common.base.Strings;
import com.hx.middleware.api.enums.StatusCode;
import com.hx.middleware.api.response.BaseResponse;
import com.hx.middleware.server.controller.BookController;
import com.hx.middleware.server.controller.lock.dto.BookRobDto;
import com.hx.middleware.server.service.lock.BookRobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jxlgcmh
 * @date 2020-02-14 08:23
 * @description
 */
@RestController
@RequestMapping("book/rob")
public class BookRobController {
    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    @Autowired
    private BookRobService bookRobService;

    @RequestMapping("/request")
    public BaseResponse rob(BookRobDto dto) {
        if (Strings.isNullOrEmpty(dto.getBookNo()) || dto.getUserId() == null || dto.getUserId() < 0) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            // 不加锁
//            bookRobService.robNoLock(dto);
            //加ZK分布式锁
//            bookRobService.robWithZKLock(dto);
            // 加Redisson分布式锁
            bookRobService.robWithRedissonLock(dto);
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }

}
