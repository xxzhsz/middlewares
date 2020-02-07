package com.hx.middleware.server.service;

import com.hx.middleware.server.dto.RedPacketDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author jxlgcmh
 * @date 2020-02-07 13:56
 * @description  发收红包记录服务
 */
public interface IRedService {

    /**
     *
     * @param redPacketDto
     * @param redId
     * @param list
     */
    void recordRedPacket(RedPacketDto redPacketDto, String redId, List<Integer> list);

    /**
     * 抢红包作记录
     * @param userId
     * @param redId
     * @param bigDecimal
     */
    void recordRobRedPacket(Integer userId, String redId, BigDecimal bigDecimal);
}
