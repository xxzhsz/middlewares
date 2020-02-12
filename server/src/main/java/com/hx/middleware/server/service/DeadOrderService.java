package com.hx.middleware.server.service;

import com.hx.middleware.model.entity.MqOrder;
import com.hx.middleware.model.entity.UserOrder;
import com.hx.middleware.model.mapper.MqOrderMapper;
import com.hx.middleware.model.mapper.UserOrderMapper;
import com.hx.middleware.server.dto.UserOrderDto;
import com.hx.middleware.server.rabbitmq.publisher.DeadOrderPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author jxlgcmh
 * @date 2020-02-12 08:15
 * @description
 */
@Service
public class DeadOrderService {

    private static final Logger log = LoggerFactory.getLogger(DeadOrderService.class);

    @Autowired
    private UserOrderMapper userOrderMapper;
    @Autowired
    private MqOrderMapper mqOrderMapper;
    @Autowired
    private DeadOrderPublisher deadOrderPublisher;

    /**
     * 用户下单操作
     *
     * @param userOrderDto
     */
    public void push(UserOrderDto userOrderDto) {
        UserOrder userOrder = new UserOrder();
        userOrder.setOrderNo(userOrderDto.getOrderNo());
        userOrder.setUserId(userOrderDto.getUserId());
        // 属性拷贝的方法，而不是对象拷贝，这是我的耻辱啊！
        // BeanUtils.copyProperties(userOrderDto,userOrder);
        userOrder.setStatus(1);
        userOrder.setCreateTime(new Date());
        userOrderMapper.insertSelective(userOrder);
        log.info("用户下单成功");
        // 将订单信息发到死信队列处理
        deadOrderPublisher.sendMsg(userOrder.getId());
    }

    /**
     * 更新用户订单操作
     *
     * @param userOrder
     */
    public void updateUserOrderRecord(UserOrder userOrder) {
        if (userOrder != null) {
            // 设置为失效
            userOrder.setIsActive(0);
            userOrder.setUpdateTime(new Date());
            userOrderMapper.updateByPrimaryKeySelective(userOrder);
            //将失效记录插入死信队列
            MqOrder mqOrder = new MqOrder();
            mqOrder.setOrderId(userOrder.getId());
            mqOrder.setBusinessTime(new Date());
            mqOrder.setMemo("更新失效订单");
            mqOrderMapper.insertSelective(mqOrder);
        }


    }
}
