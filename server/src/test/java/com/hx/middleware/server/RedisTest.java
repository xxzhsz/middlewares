package com.hx.middleware.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.model.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * @author jxlgcmh
 * @date 2020-02-06 20:26
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RedisTest {
    private static final Logger log = LoggerFactory.getLogger(RedisTest.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void one() {
        log.info("======开始redis测试============");
        final String key ="redis:template:one:string";
        final String value="RedisTemplate实战";
        redisTemplate.opsForValue().set(key,value);
        Object o = redisTemplate.opsForValue().get(key);
        log.info("读取出来的内容:{}",o);
        log.info("======结束redis测试============");
    }

    @Test
    public void two() throws IOException {
        log.info("======开始redis测试2============");
        User user =new User();
        user.setUserName("hello");
        final String key ="redis:template:two:object";
        final String value=objectMapper.writeValueAsString(user);
        redisTemplate.opsForValue().set(key,value);
        Object o = redisTemplate.opsForValue().get(key);
        if (o != null) {
            User result = objectMapper.readValue(o.toString(), User.class);
            log.info("读取出来的内容:{}",result);
        }
        log.info("======结束redis测试2============");
    }

    @Test
    public void three(){
        log.info("======开始redis测试3============");
        final String key ="redis:stringRedisTemplate:one:string";
        final String value="stringRedisTemplate实战";
        stringRedisTemplate.opsForValue().set(key,value);
        Object o = stringRedisTemplate.opsForValue().get(key);
        log.info("读取出来的内容:{}",o);
        log.info("======结束redis测试3============");
    }

    /**
     * 使用StringRedisTemplate 序列化为json存入和读取后反序列化
     * @throws IOException
     */
    @Test
    public void four() throws IOException {
        log.info("======开始redis测试4============");
        User user =new User();
        user.setUserName("hello");
        final String key ="redis:stringRedisTemplate:two:object";
        final String value=objectMapper.writeValueAsString(user);
        stringRedisTemplate.opsForValue().set(key,value);
        Object o = stringRedisTemplate.opsForValue().get(key);
        if (o != null) {
            User result = objectMapper.readValue(o.toString(), User.class);
            log.info("读取出来的内容:{}",result);
        }
        log.info("======结束redis测试4============");
    }

}
