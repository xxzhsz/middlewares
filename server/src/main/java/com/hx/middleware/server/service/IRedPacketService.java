package com.hx.middleware.server.service;

import com.hx.middleware.server.dto.RedPacketDto;

import java.math.BigDecimal;

/**
 * @author jxlgcmh
 * @date 2020-02-07 14:16
 * @description   发抢红包核心业务
 */
public interface IRedPacketService{

    /**
     * 发红包核心业务
     * @param redPacketDto
     * @return
     */
    String handOut(RedPacketDto redPacketDto) throws Exception ;

    /**
     * 抢红包
     * @param userId
     * @param redId
     * @return
     */
    BigDecimal rob(Integer userId, String redId);
}
