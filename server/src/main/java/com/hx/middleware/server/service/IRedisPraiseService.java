package com.hx.middleware.server.service;

import com.hx.middleware.model.dto.PraiseRankDto;
import com.hx.middleware.server.dto.PraiseDto;

import java.util.Collection;
import java.util.List;

/**
 * @author jxlgcmh
 * @date 2020-02-16 10:24
 * @description
 */
public interface IRedisPraiseService {
    void cachePraiseBlog(Integer blogId, Integer userId,Integer status) throws InterruptedException;
    Long getCacheTotalBlog(Integer blogId);

    List<PraiseRankDto> getBlogPraiseRank();

    /**
     * 出发博客点赞排行榜
     */
    void rankBlogPraise();
}
