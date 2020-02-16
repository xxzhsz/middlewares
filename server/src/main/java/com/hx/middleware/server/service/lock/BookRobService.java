package com.hx.middleware.server.service.lock;

import com.hx.middleware.model.entity.BookRob;
import com.hx.middleware.model.entity.BookStock;
import com.hx.middleware.model.mapper.BookRobMapper;
import com.hx.middleware.model.mapper.BookStockMapper;
import com.hx.middleware.server.controller.lock.dto.BookRobDto;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author jxlgcmh
 * @date 2020-02-14 08:37
 * @description
 */
@Service
public class BookRobService {
    private static final Logger log = LoggerFactory.getLogger(BookRobService.class);
    private static final String PATH_PREFIX = "/middleware/zkLock";
    @Autowired
    private BookStockMapper bookStockMapper;
    @Autowired
    private BookRobMapper bookRobMapper;
    @Autowired
    private CuratorFramework client;
    @Autowired
    private RedissonClient redissonClient;


    /**
     * 没有加锁
     *
     * @param dto
     */
    @Transactional(rollbackFor = Exception.class)
    public void robNoLock(BookRobDto dto) {
        BookStock bookStock = bookStockMapper.selectByBookNo(dto.getBookNo());
        int total = bookRobMapper.countByBookNoUserId(dto.getUserId(), dto.getBookNo());
        if (bookStock.getStock() != null && bookStock.getStock() > 0 && total <= 0) {
            int res = bookStockMapper.updateStock(dto.getBookNo());
            if (res > 0) {
                BookRob bookRob = new BookRob();
                BeanUtils.copyProperties(dto, bookRob);
                bookRob.setRobTime(new Date());
                bookRobMapper.insertSelective(bookRob);
                log.info("{}==>抢购成功!", dto.getUserId());
            } else {
                throw new RuntimeException("库存不足");
            }
        }
    }

    /**
     * 加ZK分布式锁
     *
     * @param dto
     */
    @Transactional(rollbackFor = Exception.class)
    public void robWithZKLock(BookRobDto dto) throws Exception {

        InterProcessMutex mutex = new InterProcessMutex(client, PATH_PREFIX + dto.getBookNo() + "-lock");

        try {
            boolean flag = mutex.acquire(15L, TimeUnit.SECONDS);
            if (flag) {
                BookStock bookStock = bookStockMapper.selectByBookNo(dto.getBookNo());
                int total = bookRobMapper.countByBookNoUserId(dto.getUserId(), dto.getBookNo());
                if (bookStock.getStock() != null && bookStock.getStock() > 0 && total <= 0) {
                    log.info("加ZK分布式锁抢书:{}", dto);
                    int res = bookStockMapper.updateStock(dto.getBookNo());
                    if (res > 0) {
                        BookRob bookRob = new BookRob();
                        BeanUtils.copyProperties(dto, bookRob);
                        bookRob.setRobTime(new Date());
                        bookRobMapper.insertSelective(bookRob);
                        log.info("{}==>抢购成功!", dto.getUserId());
                    } else {
                        throw new Exception("库存不足");
                    }
                }
            } else {
                throw new RuntimeException("获取ZK分布式锁失败!");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            // 释放锁
            mutex.release();
        }
    }


    /**
     * 加Redisson分布式锁
     *
     * @param dto
     */
    @Transactional(rollbackFor = Exception.class)
    public void robWithRedissonLock(BookRobDto dto) throws Exception {
        // 这样定义锁是否会有问题
        final String lockName = "RedissonTryLock-" + dto.getBookNo() + dto.getUserId();
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean flag = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (flag) {
                BookStock bookStock = bookStockMapper.selectByBookNo(dto.getBookNo());
                int total = bookRobMapper.countByBookNoUserId(dto.getUserId(), dto.getBookNo());
                if (bookStock.getStock() != null && bookStock.getStock() > 0 && total <= 0) {
                    log.info("加Redisson分布式锁抢书:{}", dto);
                    /**
                     * 不允许出现超买情况
                     * [2020-02-16 09:58:29.048] boot -  INFO [http-nio-8087-exec-111] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1007, bookNo=BS20190421001)
                     * [2020-02-16 09:58:29.050] boot -  INFO [http-nio-8087-exec-97] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1005, bookNo=BS20190421001)
                     * [2020-02-16 09:58:29.058] boot -  INFO [http-nio-8087-exec-121] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1004, bookNo=BS20190421001)
                     * [2020-02-16 09:58:29.060] boot -  INFO [http-nio-8087-exec-29] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1003, bookNo=BS20190421001)
                     * [2020-02-16 09:58:29.060] boot -  INFO [http-nio-8087-exec-33] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1000, bookNo=BS20190421001)
                     * [2020-02-16 09:58:29.068] boot -  INFO [http-nio-8087-exec-53] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1002, bookNo=BS20190421001)
                     * [2020-02-16 09:58:29.080] boot -  INFO [http-nio-8087-exec-135] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1008, bookNo=BS20190421001)
                     * [2020-02-16 09:58:29.080] boot -  INFO [http-nio-8087-exec-98] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1006, bookNo=BS20190421001)
                     * [2020-02-16 09:58:29.129] boot -  INFO [http-nio-8087-exec-163] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1001, bookNo=BS20190421001)
                     * [2020-02-16 09:58:29.179] boot -  INFO [http-nio-8087-exec-164] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1009, bookNo=BS20190421001)
                     * [2020-02-16 09:58:29.189] boot -  INFO [http-nio-8087-exec-111] --- BookRobService: 1007==>抢购成功!
                     * [2020-02-16 09:58:29.479] boot -  INFO [http-nio-8087-exec-97] --- BookRobService: 1005==>抢购成功!
                     */
                    //int res = bookStockMapper.updateStockWithLock(dto.getBookNo());
                    /** 下面试允许出现超买情况
                     * [2020-02-16 10:01:49.650] boot -  INFO [http-nio-8087-exec-27] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1005, bookNo=BS20190421001)
                     * [2020-02-16 10:01:49.650] boot -  INFO [http-nio-8087-exec-170] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1009, bookNo=BS20190421001)
                     * [2020-02-16 10:01:49.660] boot -  INFO [http-nio-8087-exec-71] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1004, bookNo=BS20190421001)
                     * [2020-02-16 10:01:49.660] boot -  INFO [http-nio-8087-exec-58] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1006, bookNo=BS20190421001)
                     * [2020-02-16 10:01:49.660] boot -  INFO [http-nio-8087-exec-79] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1007, bookNo=BS20190421001)
                     * [2020-02-16 10:01:49.660] boot -  INFO [http-nio-8087-exec-17] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1002, bookNo=BS20190421001)
                     * [2020-02-16 10:01:49.660] boot -  INFO [http-nio-8087-exec-21] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1008, bookNo=BS20190421001)
                     * [2020-02-16 10:01:49.680] boot -  INFO [http-nio-8087-exec-25] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1003, bookNo=BS20190421001)
                     * [2020-02-16 10:01:49.680] boot -  INFO [http-nio-8087-exec-82] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1001, bookNo=BS20190421001)
                     * [2020-02-16 10:01:49.680] boot -  INFO [http-nio-8087-exec-2] --- BookRobService: 加Redisson分布式锁抢书:BookRobDto(userId=1000, bookNo=BS20190421001)
                     * [2020-02-16 10:01:49.710] boot -  INFO [http-nio-8087-exec-27] --- BookRobService: 1005==>抢购成功!
                     * [2020-02-16 10:01:49.850] boot -  INFO [http-nio-8087-exec-170] --- BookRobService: 1009==>抢购成功!
                     * [2020-02-16 10:01:50.020] boot -  INFO [http-nio-8087-exec-71] --- BookRobService: 1004==>抢购成功!
                     * [2020-02-16 10:01:50.190] boot -  INFO [http-nio-8087-exec-58] --- BookRobService: 1006==>抢购成功!
                     * [2020-02-16 10:01:50.490] boot -  INFO [http-nio-8087-exec-79] --- BookRobService: 1007==>抢购成功!
                     * [2020-02-16 10:01:50.640] boot -  INFO [http-nio-8087-exec-17] --- BookRobService: 1002==>抢购成功!
                     * [2020-02-16 10:01:50.790] boot -  INFO [http-nio-8087-exec-21] --- BookRobService: 1008==>抢购成功!
                     * [2020-02-16 10:01:50.900] boot -  INFO [http-nio-8087-exec-25] --- BookRobService: 1003==>抢购成功!
                     * [2020-02-16 10:01:51.030] boot -  INFO [http-nio-8087-exec-2] --- BookRobService: 1000==>抢购成功!
                     * [2020-02-16 10:01:51.140] boot -  INFO [http-nio-8087-exec-82] --- BookRobService: 1001==>抢购成功!
                     */
                    int res = bookStockMapper.updateStock(dto.getBookNo());
                    if (res > 0) {
                        BookRob bookRob = new BookRob();
                        BeanUtils.copyProperties(dto, bookRob);
                        bookRob.setRobTime(new Date());
                        bookRobMapper.insertSelective(bookRob);
                        log.info("{}==>抢购成功!", dto.getUserId());
                    } else {
                        throw new Exception("库存不足");
                    }
                }
            } else {
                throw new Exception("获取Redisson分布式锁失败!");
            }
        } catch (Exception ignored) {
            throw  ignored;
        } finally {
            if (lock != null) {
                lock.unlock();
                //
                //lock.forceUnlock();
            }
        }


    }
}
