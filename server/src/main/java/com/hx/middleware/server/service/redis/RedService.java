package com.hx.middleware.server.service.redis;

import com.hx.middleware.model.entity.RedDetail;
import com.hx.middleware.model.entity.RedRecord;
import com.hx.middleware.model.entity.RedRobRecord;
import com.hx.middleware.model.mapper.RedDetailMapper;
import com.hx.middleware.model.mapper.RedRecordMapper;
import com.hx.middleware.model.mapper.RedRobRecordMapper;
import com.hx.middleware.server.controller.redis.RedPacketController;
import com.hx.middleware.server.dto.RedPacketDto;
import com.hx.middleware.server.service.IRedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author jxlgcmh
 * @date 2020-02-07 14:02
 * @description
 */
@Service
@EnableAsync
public class RedService implements IRedService {
    private static final Logger log = LoggerFactory.getLogger(RedService.class);

    @Autowired
    private RedRecordMapper redRecordMapper;

    @Autowired
    private RedRobRecordMapper redRobRecordMapper;
    @Autowired
    private RedDetailMapper redDetailMapper;

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void recordRedPacket(RedPacketDto redPacketDto, String redId, List<Integer> list) {
        //红包记录
        RedRecord redRecord = new RedRecord();
        redRecord.setUserId(redPacketDto.getUserId());
        redRecord.setRedPacket(redId);
        redRecord.setTotal(redPacketDto.getTotal());
        redRecord.setAmount(BigDecimal.valueOf(redPacketDto.getAmount()));
        redRecord.setCreateTime(new Date());
        redRecordMapper.insertSelective(redRecord);
        // 红包详情记录
        RedDetail redDetail;
        for (Integer amount : list) {
            redDetail = new RedDetail();
            redDetail.setRecordId(redRecord.getId());
            redDetail.setAmount(BigDecimal.valueOf(amount));
            redDetail.setCreateTime(new Date());
            redDetailMapper.insertSelective(redDetail);
        }
    }

    @Override
    public void recordRobRedPacket(Integer userId, String redId, BigDecimal bigDecimal) {
        RedRobRecord redRobRecord = new RedRobRecord();
        redRobRecord.setUserId(userId);
        redRobRecord.setRedPacket(redId);
        redRobRecord.setAmount(bigDecimal);
        redRobRecord.setRobTime(new Date());
        redRobRecordMapper.insertSelective(redRobRecord);
    }
}
