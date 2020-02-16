package com.hx.middleware.server.controller;

import com.google.common.collect.Maps;
import com.hx.middleware.api.enums.StatusCode;
import com.hx.middleware.api.response.BaseResponse;
import com.hx.middleware.model.dto.PraiseRankDto;
import com.hx.middleware.server.dto.PraiseDto;
import com.hx.middleware.server.dto.RedPacketDto;
import com.hx.middleware.server.service.IPraiseService;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jxlgcmh
 * @date 2020-02-16 10:23
 * @description
 */
@RestController
@RequestMapping("blog/praise")
public class PraiseController {
    private static final Logger log = LoggerFactory.getLogger(PraiseController.class);

    @Autowired
    private IPraiseService praiseService;


    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse addPrise(@RequestBody @Validated PraiseDto dto, BindingResult result) {
        if (result.hasErrors()) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        HashMap<String, Object> map = Maps.newHashMap();
        try {
            // 普通点赞 不加锁
            // praiseService.addPraise(dto);
            //
            praiseService.addPraiseWithLock(dto);
            // 获取点赞总数
            Long total = praiseService.getBlogPraiseTotal(dto.getBlogId());
            map.put("PraiseTotal", total);

        } catch (Exception e) {
            log.error("点赞博客发生异常:{}", dto, e.fillInStackTrace());
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        response.setData(map);
        return response;
    }


    @RequestMapping(value = "/cancel", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse cancelPrise(@RequestBody @Validated PraiseDto dto, BindingResult result) {
        if (result.hasErrors()) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        HashMap<String, Object> map = Maps.newHashMap();
        try {
            // 普通点赞 不加锁
            praiseService.cancelPraise(dto);
            // 获取点赞总数
            Long total = praiseService.getBlogPraiseTotal(dto.getBlogId());
            map.put("PraiseTotal", total);
        } catch (Exception e) {
            log.error("取消点赞博客发生异常:{}", dto, e.fillInStackTrace());
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        response.setData(map);
        return response;
    }

    @RequestMapping(value = "/total/rank", method = RequestMethod.GET)
    public BaseResponse rankPrise() {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            // 带锁的方式获取点赞
            Collection<PraiseRankDto> rankWithRedisson = praiseService.getRankWithRedisson();
            //不带锁
//            Collection<PraiseRankDto> rankWithNoRedisson = praiseService.getRankWithNoRedisson();
            response.setData(rankWithRedisson);
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
            log.error("获取博客点赞排行榜-出现异常", e.fillInStackTrace());
        }
        return response;
    }

}
