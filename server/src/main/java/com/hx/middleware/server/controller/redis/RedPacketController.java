package com.hx.middleware.server.controller.redis;

import com.hx.middleware.api.enums.StatusCode;
import com.hx.middleware.api.response.BaseResponse;
import com.hx.middleware.model.dto.RedPacketDto;
import com.hx.middleware.server.service.IRedPacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author jxlgcmh
 * @date 2020-02-07 13:53
 * @description
 */
@RestController
@RequestMapping("/red/packet")
public class RedPacketController {
    private static final Logger log = LoggerFactory.getLogger(RedPacketController.class);

    @Autowired
    private IRedPacketService redPacketService;

    /**
     *
     * @param redPacketDto
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/hand/out",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse handout(@Validated @RequestBody RedPacketDto redPacketDto , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response =new BaseResponse(StatusCode.Success);
        try {
            String redId = redPacketService.handOut(redPacketDto);
            response.setData(redId);
        } catch (Exception e) {
            log.error("发生异常:redPacketDto={}",redPacketDto, e.fillInStackTrace());
           response =new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }


    @RequestMapping(value = "/rob",method = RequestMethod.GET)
    public BaseResponse rob(@RequestParam Integer userId, @RequestParam String redId) {
        BaseResponse response =new BaseResponse(StatusCode.Success);
        try {
            BigDecimal result=redPacketService.rob(userId,redId);
            if (result != null) {
                response.setData(result);
            }else {
                response =new BaseResponse(StatusCode.Fail.getCode(),"红包已抢完");
            }
        } catch (Exception e) {
            log.error("抢红包发生异常，userId={},redId={}",userId,redId,e.fillInStackTrace());
            response =new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

}
