package com.hx.middleware.server;

import com.hx.middleware.server.dto.BloomDto;
import com.hx.middleware.server.dto.RMapDto;
import com.hx.middleware.server.dto.RSetDto;
import com.hx.middleware.server.dto.UserLoginDto;
import com.hx.middleware.server.service.redisson.UserLoginPublisher;
import com.hx.middleware.server.util.RSetComparator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author jxlgcmh
 * @date 2020-02-14 21:27
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RedissonTest {
     private static final Logger log = LoggerFactory.getLogger(RedissonTest.class);
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private UserLoginPublisher userLoginPublisher;

    @Test
    public void one() throws IOException {
        log.info("Redisson客户端信息:{}",redissonClient.getConfig().toJSON());
    }
    @Test
    public void two() throws IOException {
        final String key ="MyBloomFilter";
        Long total = 100L;
        RBloomFilter<Integer> bloomFilter = redissonClient.getBloomFilter(key);
        //元素总量为total,误差为0.01
        bloomFilter.tryInit(total,0.01);
        for (int i = 1; i <= total; i++) {
            bloomFilter.add(i);
        }
        log.info("布隆过滤器中是否包含-1:{}",bloomFilter.contains(-1));
        log.info("布隆过滤器中是否包含1:{}",bloomFilter.contains(1));
        log.info("布隆过滤器中是否包含10000:{}",bloomFilter.contains(10000));
        log.info("布隆过滤器中是否包含1000000:{}",bloomFilter.contains(1000000));
    }

    @Test
    public void three() {
        final String key ="MyBloomFilterV3";
        RBloomFilter<BloomDto> filter = redissonClient.getBloomFilter(key);
        filter.tryInit(1000L,0.01);
        BloomDto dto1 =new BloomDto(1,"lucy1");
        BloomDto dto2 =new BloomDto(2,"lucy2");
        BloomDto dto3 =new BloomDto(3,"lucy3");
        BloomDto dto4 =new BloomDto(4,"lucy4");
        filter.add(dto1);
        filter.add(dto2);
        filter.add(dto3);
        filter.add(dto4);
        log.info("是否包含dto1,{}",filter.contains(dto1));
        log.info("是否包含dtoX,{}",filter.contains(new BloomDto(99,"2")));
    }

    @Test
    public void four() {
        UserLoginDto dto =new UserLoginDto();
        dto.setUserId(10010);
        dto.setUserName("lily");
        dto.setPassword("123456");
        userLoginPublisher.sendMsg(dto);
    }
    @Test
    public void five() {
        final String key ="redissonRMap";
        RMapDto dto1 = new RMapDto(1,"A");
        RMapDto dto2 = new RMapDto(2,"B");
        RMapDto dto3 = new RMapDto(3,"C");
        RMapDto dto4 = new RMapDto(4,"D");
        RMap<Integer, RMapDto> map = redissonClient.getMap(key);
        /**
         * map 继承了Map  和ConcurrentMap接口  功能强大,自测
         */
        map.put(dto1.getId(),dto1);
        map.putIfAbsent(dto1.getId(),dto1);
        map.putAsync(dto2.getId(),dto2);
        map.putIfAbsentAsync(dto2.getId(),dto2);
        map.fastPut(dto3.getId(),dto3);
        map.fastPutIfAbsent(dto3.getId(),dto3);
        map.fastPutIfAbsentAsync(dto4.getId(),dto4);
        log.info("往Redisson的map中添加数据");

    }
    @Test
    public void six() {
        final String key = "redissonRMap";
        RMap<Integer, RMapDto> map = redissonClient.getMap(key);
        for (Map.Entry<Integer, RMapDto> entry : map.entrySet()) {
            log.info("key:{},value:{}",entry.getKey(),entry.getValue());
        }
        map.remove(1);
        for (Map.Entry<Integer, RMapDto> entry : map.entrySet()) {
            log.info("key:{},value:{}",entry.getKey(),entry.getValue());
        }
    }

    @Test
    public void seven() throws InterruptedException {
        final String key = "redissonMapCache";
        RMapDto dto1 = new RMapDto(1, "A");
        RMapDto dto2 = new RMapDto(2, "B");
        RMapDto dto3 = new RMapDto(3, "C");
        RMapDto dto4 = new RMapDto(4, "D");
        RMapCache<Integer, RMapDto> mapCache = redissonClient.getMapCache(key);
        mapCache.put(dto1.getId(), dto1);
        mapCache.putAsync(dto2.getId(), dto2, 5, TimeUnit.SECONDS);
        mapCache.fastPutIfAbsent(dto3.getId(), dto3);
        mapCache.fastPutIfAbsentAsync(dto4.getId(), dto4);
        for (Map.Entry<Integer, RMapDto> entry : mapCache.entrySet()) {
            log.info("key:{},value:{}", entry.getKey(), entry.getValue());
        }
        Thread.sleep(6000);
        log.info("=================================================");
        for (Map.Entry<Integer, RMapDto> entry : mapCache.entrySet()) {
            log.info("key:{},value:{}", entry.getKey(), entry.getValue());
        }
    }

    @Test
    public void eight() {
        final String key = "redissonRSet";
        RSetDto dto1 = new RSetDto(1,"A",15,60D);
        RSetDto dto2 = new RSetDto(2,"B",26,61D);
        RSetDto dto3 = new RSetDto(3,"C",7,62D);
        RSetDto dto4 = new RSetDto(4,"D",18,63D);

        RSortedSet<RSetDto> set = redissonClient.getSortedSet(key);
        set.trySetComparator(new RSetComparator());
        set.add(dto1);
        set.add(dto2);
        set.add(dto3);
        set.add(dto4);

        Collection<RSetDto> all = set.readAll();
        log.info("此时set集合里的元素有:{}",all);

    }

    @Test
    public void nine() {
        final String key = "redissonSortedSet";
        RSetDto dto1 = new RSetDto(1,"A",15,60D);
        RSetDto dto2 = new RSetDto(2,"B",26,61D);
        RSetDto dto3 = new RSetDto(3,"C",7,62D);
        RSetDto dto4 = new RSetDto(4,"D",18,63D);

        RScoredSortedSet<RSetDto> sortedSet = redissonClient.getScoredSortedSet(key);
        sortedSet.add(dto1.getScore(),dto1);
        sortedSet.add(dto2.getScore(),dto2);
        sortedSet.add(dto3.getScore(),dto3);
        sortedSet.add(dto4.getScore(),dto4);
        Set<RSetDto> all = sortedSet.readSortAlpha(SortOrder.DESC);

        log.info("此时sortedSet集合里的元素有从大到小有:{}",all);
        log.info("dto1分数:{}",sortedSet.getScore(dto1));
        log.info("dto2分数:{}",sortedSet.getScore(dto2));
        log.info("dto5分数:{}",sortedSet.getScore(dto3));
        log.info("dto4分数:{}",sortedSet.getScore(dto4));

    }
}
