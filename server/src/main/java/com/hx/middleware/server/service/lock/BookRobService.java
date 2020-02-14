package com.hx.middleware.server.service.lock;

import com.hx.middleware.model.entity.BookRob;
import com.hx.middleware.model.entity.BookStock;
import com.hx.middleware.model.mapper.BookRobMapper;
import com.hx.middleware.model.mapper.BookStockMapper;
import com.hx.middleware.server.controller.lock.dto.BookRobDto;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
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
                    log.info("加ZK分布式锁抢书:{}",dto);
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
            }else {
                throw new RuntimeException("获取ZK分布式锁失败!");
            }
        } catch (Exception e) {
           throw e;
        }finally {
            // 释放锁
            mutex.release();
        }
    }
}
