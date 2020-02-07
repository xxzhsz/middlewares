package com.hx.middleware.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.server.entity.Person;
import com.hx.middleware.server.entity.User;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author jxlgcmh
 * @date 2020-02-06 20:26
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RedisTest2 {
    private static final Logger log = LoggerFactory.getLogger(RedisTest2.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void one() throws IOException {
        log.info("======开始redis测试2============");
        Person person =new Person(1,"lili",3);
        final String key ="redis:test:1";
        final String value=objectMapper.writeValueAsString(person);
        redisTemplate.opsForValue().set(key,value);
        Object o = redisTemplate.opsForValue().get(key);
        if (o != null) {
            Person result = objectMapper.readValue(o.toString(), Person.class);
            log.info("读取出来的内容:{}",result);
        }
        log.info("======结束redis测试2============");
    }

    @Test
    public void two() throws IOException {
        log.info("======开始测试============");
        List<Person> list = new ArrayList<>();
        list.add(new Person(1,"lili1",3));
        list.add(new Person(2,"lili2",4));
        list.add(new Person(3,"lili3",5));
        list.add(new Person(4,"lili4",6));

        final String key ="redis:test:2";
        for (Person person : list) {
            redisTemplate.opsForList().leftPush(key,person);
        }
//        Object object = redisTemplate.opsForList().rightPop(key);
//        while (object != null) {
//            Person result = (Person)object;
//            object= redisTemplate.opsForList().rightPop(key);
//            log.info("读取出来的内容:{}",result);
//        }
//        log.info("======结束测试============");
    }
}
