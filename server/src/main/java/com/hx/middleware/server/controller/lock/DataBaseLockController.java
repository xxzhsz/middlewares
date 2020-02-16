package com.hx.middleware.server.controller.lock;

import com.hx.middleware.api.enums.StatusCode;
import com.hx.middleware.api.response.BaseResponse;
import com.hx.middleware.model.dto.UserAccountDto;
import com.hx.middleware.server.service.lock.DataBaseLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jxlgcmh
 * @date 2020-02-13 15:03
 * @description
 */
@RestController
@RequestMapping("db")
public class DataBaseLockController {
    private static final Logger log = LoggerFactory.getLogger(DataBaseLockController.class);

    @Autowired
    private DataBaseLockService dataBaseLockService;


    @RequestMapping(value = "/money/withdraw", method = RequestMethod.GET)
    public BaseResponse withdraw(UserAccountDto userAccountDto) {
        if (userAccountDto.getAmount() == null || userAccountDto.getUserId() == null) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            // 尝试无锁取款
//             dataBaseLockService.withdrawNoLock(userAccountDto);
            // 乐观所
//             dataBaseLockService.withdrawWithLock(userAccountDto);
            // 悲观锁处理
            dataBaseLockService.withdrawWithLockNegative(userAccountDto);
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }

}
