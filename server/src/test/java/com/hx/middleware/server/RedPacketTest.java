package com.hx.middleware.server;

import com.hx.middleware.server.util.RedPacketUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author jxlgcmh
 * @date 2020-02-07 13:41
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RedPacketTest {
    private static final Logger log = LoggerFactory.getLogger(RedPacketTest.class);

    @Test
    public void test() {
        List<Integer> list = RedPacketUtil.dividedRedPacket(100, 10);
        int sum=0;
        for (Integer amount : list) {
            log.info("随机金额为:{}分，即{}元",amount,new BigDecimal(amount.toString()).divide(new BigDecimal(100)));
            sum+=amount;
        }
        log.info("总金额为:{}分",sum);
    }
}
