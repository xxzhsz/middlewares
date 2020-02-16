package com.hx.middleware.server.service.redis;

import com.hx.middleware.model.dto.RedPacketDto;
import com.hx.middleware.server.service.IRedPacketService;
import com.hx.middleware.server.service.IRedService;
import com.hx.middleware.server.util.RedPacketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author jxlgcmh
 * @date 2020-02-07 14:25
 * @description
 */
@Service
public class RedPacketService implements IRedPacketService {
    private static final Logger log = LoggerFactory.getLogger(RedPacketService.class);
    private static final String KEY_PREFIX = "redis:red:packet:";

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IRedService redService;

    /**
     * ===红包的key设置的不怎么好
     *
     * @param redPacketDto
     * @return
     * @throws Exception
     */
    @Override
    public String handOut(RedPacketDto redPacketDto) throws Exception {
        // 判断入参是否合法
        if (redPacketDto.getAmount() > 0 && redPacketDto.getTotal() > 0) {
            List<Integer> list = RedPacketUtil.dividedRedPacket(redPacketDto.getAmount(), redPacketDto.getTotal());
            String timestamp = String.valueOf(System.nanoTime());
            // 红包key
            String redId = KEY_PREFIX + redPacketDto.getUserId() + ":" + timestamp;
            // 将红包随机金额放入redis
            redisTemplate.opsForList().leftPushAll(redId, list);
            // 红包个数key
            String redTotalKey = redId + ":total";
            //红包个数放入redis
            redisTemplate.opsForValue().set(redTotalKey, redPacketDto.getTotal());
            // 数据库中记录红包
            redService.recordRedPacket(redPacketDto, redId, list);
            return redId;
        } else {
            throw new Exception("参数不合法，发送红包失败！");
        }
    }

    @Override
    public BigDecimal rob(Integer userId, String redId) {
        // 抢红包者记录key
        final String USER_KEY_PREFIX = redId + ":rob:" + userId;
        // 如果用户抢过了就直接显示数据
        Object object = redisTemplate.opsForValue().get(USER_KEY_PREFIX);
        if (object != null) {
            return new BigDecimal(object.toString());
        }
        // 点击红包
        Boolean result = click(redId);
        if (result) {
            // 添加分布式锁  === 防止某一用户抢多次
            final String lockKey = redId + userId + "-lock";
            // 如果不存在返回true并设置
            Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, redId);
            redisTemplate.expire(lockKey, 24L, TimeUnit.HOURS);
            try {
                if (isLock) {
                    // 从list中取一个红包出来
                    Object value = redisTemplate.opsForList().rightPop(redId);
                    if (value != null) {
                        //对应的红包个数减去1
                        String totalKey = redId + ":total";
                        // 当前的红包个数总数
                        Integer currentTotal = redisTemplate.opsForValue().get(totalKey) != null ? (Integer) redisTemplate.opsForValue().get(totalKey) : 0;
                        // 当前的减1
                        redisTemplate.opsForValue().set(totalKey, currentTotal - 1);
                        //单位转换
                        BigDecimal res = new BigDecimal(value.toString()).divide(new BigDecimal(100));
                        //记录抢到的钱
                        redService.recordRobRedPacket(userId, redId, new BigDecimal(value.toString()));
                        // 将抢到红包的钱记录存入缓存
                        redisTemplate.opsForValue().set(USER_KEY_PREFIX, res, 24L, TimeUnit.HOURS);
                        log.info("红包金额为:{}", res);
                        return res;
                    }
                }
            } catch (Exception e){
                throw  new RuntimeException("系统异常=抢红包=加分布式锁失败");
            }
        }
        // 点击后没抢到
        return null;
    }

    private Boolean click(String redId) {
        String totalKey = redId + ":total";
        Object total = redisTemplate.opsForValue().get(totalKey);
        // 红包还有个数
        if (total != null && Integer.parseInt(total.toString()) > 0) {
            return true;
        }
        return false;
    }
}
