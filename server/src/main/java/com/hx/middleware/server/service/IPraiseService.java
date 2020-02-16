package com.hx.middleware.server.service;

import com.hx.middleware.model.dto.PraiseDto;
import com.hx.middleware.model.dto.PraiseRankDto;

import java.util.Collection;

/**
 * @author jxlgcmh
 * @date 2020-02-16 10:53
 * @description
 */
public interface IPraiseService {
    /**
     * 点赞不加锁
     * @param dto
     * @throws InterruptedException
     */
    void addPraise(PraiseDto dto) throws InterruptedException;
    /**
     * 添加分布式锁
     * @param dto
     */
    void addPraiseWithLock(PraiseDto dto) throws InterruptedException;

    /**
     * 取消点赞
     * @param dto
     * @throws InterruptedException
     */
    void cancelPraise(PraiseDto dto) throws InterruptedException;
    /**
     * 获取博客的点赞数
     * @param blogId
     * @return
     */
    Long getBlogPraiseTotal(Integer blogId);

    /**
     * 获取排行榜加锁
     * @return
     */
    Collection<PraiseRankDto> getRankWithRedisson();

    /**
     * 获取排行榜不加锁
     * @return
     */
    Collection<PraiseRankDto> getRankWithNoRedisson();
}
