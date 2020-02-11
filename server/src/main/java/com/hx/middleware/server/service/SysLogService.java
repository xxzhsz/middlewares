package com.hx.middleware.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.model.entity.SysLog;
import com.hx.middleware.model.mapper.SysLogMapper;
import com.hx.middleware.server.dto.UserLoginDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author jxlgcmh
 * @date 2020-02-11 20:55
 * @description
 */
@Service
public class SysLogService {
    private static final Logger log = LoggerFactory.getLogger(SysLogService.class);
    @Autowired
    private SysLogMapper sysLogMapper;
    @Autowired
    private ObjectMapper objectMapper;

    public void recordLoginLog(UserLoginDto loginDto) {
        try {
            SysLog sysLog = new SysLog();
            sysLog.setUserId(loginDto.getUserId());
            sysLog.setModule("用户登录模块");
            sysLog.setData(objectMapper.writeValueAsString(loginDto));
            sysLog.setMemo("用户登录成功");
            sysLog.setCreateTime(new Date());
            sysLogMapper.insertSelective(sysLog);
        } catch (JsonProcessingException e) {
            log.error("记录用户登录日志发生异常", e.fillInStackTrace());
        }
    }
}
