package com.hx.middleware.server.service.lock;

import com.hx.middleware.model.dto.UserRegDto;
import com.hx.middleware.model.entity.UserReg;
import com.hx.middleware.model.mapper.UserRegMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author jxlgcmh
 * @date 2020-02-13 16:45
 * @description
 */
@Service
public class UserRegService {
    private static final Logger log = LoggerFactory.getLogger(UserRegService.class);

    @Autowired
    private UserRegMapper userRegMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CuratorFramework zkClient;
    @Autowired
    private RedissonClient redissonClient;

    private static final String pathPrefix = "/middleware/zkLock";


    public void regNoLock(UserRegDto dto) {
        UserReg user = userRegMapper.selectByUserName(dto.getUserName());
        if (user == null) {
            log.info("没有分布式锁,注册的用户名为:{}", dto.getUserName());
            UserReg userReg = new UserReg();
            BeanUtils.copyProperties(dto, userReg);
            userReg.setCreateTime(new Date());
            userRegMapper.insertSelective(userReg);
        } else {
            throw new RuntimeException("用户名已存在");
        }
    }

    /**
     * 分布式锁
     * @param dto
     */
    public void regWithDistributeLock(UserRegDto dto) {
        // 这是一个标志,需要精心设计
        final String key = dto.getUserName() + "-lock";
        final String value = System.nanoTime() + "" + UUID.randomUUID();
        // 这就是一个锁标志
        Boolean absent = stringRedisTemplate.opsForValue().setIfAbsent(key, value);
        if (absent) {
            /**
             * 防止Redis死锁  https://www.cnblogs.com/ljy-skill/p/10789294.html
             * 注册过程中出现异常  没有注册成功
             * 要把锁释放  后来者可以注册
              */
            stringRedisTemplate.expire(key,20L, TimeUnit.SECONDS);
            try {
                UserReg user = userRegMapper.selectByUserName(dto.getUserName());
                if (user == null) {
                    log.info("分布式锁,注册的用户名为:{}", dto.getUserName());
                    UserReg userReg = new UserReg();
                    BeanUtils.copyProperties(dto, userReg);
                    userReg.setCreateTime(new Date());
                    userRegMapper.insertSelective(userReg);
                } else {
                    throw new RuntimeException("用户名已存在");
                }
            } catch (RuntimeException e) {
               throw e;
            }finally {
                // 无论如何  操作完毕之后要释放锁
                if (value.equals(stringRedisTemplate.opsForValue().get(key))) {
                    stringRedisTemplate.delete(key);
                }
            }
        }
    }

    public void regWithZkDistributeLock(UserRegDto dto) throws Exception {
        InterProcessMutex mutex = new InterProcessMutex(zkClient, pathPrefix + dto.getUserName() + "-lock");
        try {
            boolean res = mutex.acquire(10L, TimeUnit.SECONDS);
            if (res) {
                UserReg user = userRegMapper.selectByUserName(dto.getUserName());
                if (user == null) {
                    log.info("加ZK分布式锁,注册的用户名为:{}", dto.getUserName());
                    UserReg userReg = new UserReg();
                    BeanUtils.copyProperties(dto, userReg);
                    userReg.setCreateTime(new Date());
                    userRegMapper.insertSelective(userReg);
                } else {
                    throw new RuntimeException("用户名已存在");
                }
            }else {
                throw new RuntimeException("获取zk分布式锁失败!");
            }
        } catch (Exception e) {
            throw e;
        }finally {
            // 释放锁
            mutex.release();
        }
    }

    /**
     * 通过redisson分布式锁来开发注册服务
     * @param dto
     */
    public void RegWithRedissonDistributeLock(UserRegDto dto) {
        final String lockName = "RedissonLock-"+dto.getUserName();
        // 通过并发测试来看,这个锁是大家都能获取到的
        // 这个锁是一次行锁
        RLock lock = redissonClient.getLock(lockName);
        try {
            // 不管何种情况,先锁上他10秒钟
            lock.lock(10L,TimeUnit.SECONDS);
            UserReg user = userRegMapper.selectByUserName(dto.getUserName());
            if (user == null) {
                log.info("加Redisson分布式锁,注册的用户名为:{}", dto.getUserName());
                UserReg userReg = new UserReg();
                BeanUtils.copyProperties(dto, userReg);
                userReg.setCreateTime(new Date());
                userRegMapper.insertSelective(userReg);
            }
        }catch (Exception ignored){
            log.error("获取redisson分布式锁出现异常!");
            throw ignored;
        }finally {
            // 无论如何也要释放锁
            if (lock != null) {
                lock.unlock();
                //强制释放
                //lock.forceUnlock();
            }
        }

    }

}
