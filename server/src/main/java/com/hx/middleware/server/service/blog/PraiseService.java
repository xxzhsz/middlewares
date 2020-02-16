package com.hx.middleware.server.service.blog;

import com.hx.middleware.model.dto.PraiseDto;
import com.hx.middleware.model.dto.PraiseRankDto;
import com.hx.middleware.model.entity.Praise;
import com.hx.middleware.model.mapper.PraiseMapper;
import com.hx.middleware.server.service.IPraiseService;
import com.hx.middleware.server.service.IRedisPraiseService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author jxlgcmh
 * @date 2020-02-16 10:49
 * @description
 */
@Service
public class PraiseService implements IPraiseService {
    private static final Logger log = LoggerFactory.getLogger(PraiseService.class);
    private static final String keyAddBlogLock = "RedisBlogPraiseAddLock";

    @Autowired
    private Environment env;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private PraiseMapper praiseMapper;
    @Autowired
    private IRedisPraiseService redisPraiseService;

    @Override
    public void addPraise(PraiseDto dto) throws InterruptedException {
        Praise praise = praiseMapper.selectByBlogUserId(dto.getBlogId(), dto.getUserId());
        // 没有点过赞
        if (praise == null) {
            praise = new Praise();
            BeanUtils.copyProperties(dto, praise);
            praise.setPraiseTime(new Date());
            // 1 表示正常赞的状态
            praise.setStatus(1);
            int res = praiseMapper.insertSelective(praise);
            if (res > 0) {
                log.info("userId:{}点赞BlogId:{}成功", dto.getUserId(), dto.getBlogId());
                // 加入到缓存中
                redisPraiseService.cachePraiseBlog(dto.getBlogId(), dto.getUserId(), 1);
                // 排行榜功能待实现
                this.cachePraiseTotal();
            }
        }

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelPraise(PraiseDto dto) throws InterruptedException {
        if (dto.getUserId() != null && dto.getBlogId() != null) {
            int res = praiseMapper.cancelPraiseBlog(dto.getBlogId(), dto.getUserId());
            if (res > 0) {
                log.info("userId:{}取消赞BlogId:{}成功", dto.getUserId(), dto.getBlogId());
                redisPraiseService.cachePraiseBlog(dto.getBlogId(), dto.getUserId(), 0);
                // 排行榜功能待实现
                // 排行榜功能待实现
                this.cachePraiseTotal();
            }
        }
    }

    /**
     *  解决重复点赞的问题
     * @param dto
     * @throws InterruptedException
     */
    @Override
    public void addPraiseWithLock(PraiseDto dto) throws InterruptedException {
        final String lockName = keyAddBlogLock + dto.getBlogId() + "-" + dto.getUserId();
        RLock lock = redissonClient.getLock(lockName);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            Praise praise = praiseMapper.selectByBlogUserId(dto.getBlogId(), dto.getUserId());
            if (praise == null) {
                praise = new Praise();
                BeanUtils.copyProperties(dto, praise);
                praise.setPraiseTime(new Date());
                // 1 表示正常赞的状态
                praise.setStatus(1);
                int res = praiseMapper.insertSelective(praise);
                if (res > 0) {
                    log.info("userId:{}点赞BlogId:{}成功", dto.getUserId(), dto.getBlogId());
                    // 加入到缓存中
                    redisPraiseService.cachePraiseBlog(dto.getBlogId(), dto.getUserId(), 1);
                    // 排行榜功能待实现
                    this.cachePraiseTotal();
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    @Override
    public Collection<PraiseRankDto> getRankWithRedisson() {
        return redisPraiseService.getBlogPraiseRank();
    }

    @Override
    public Collection<PraiseRankDto> getRankWithNoRedisson() {
        return praiseMapper.getPraiseRank();
    }


    private void cachePraiseTotal() {
        try {
            redisPraiseService.rankBlogPraise();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public Long getBlogPraiseTotal(Integer blogId) {
        return redisPraiseService.getCacheTotalBlog(blogId);
    }
}
