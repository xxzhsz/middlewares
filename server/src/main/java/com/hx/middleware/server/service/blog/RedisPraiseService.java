package com.hx.middleware.server.service.blog;

import com.hx.middleware.model.dto.PraiseRankDto;
import com.hx.middleware.model.mapper.PraiseMapper;
import com.hx.middleware.server.service.IPraiseService;
import com.hx.middleware.server.service.IRedisPraiseService;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author jxlgcmh
 * @date 2020-02-16 11:04
 * @description
 */
@Service
public class RedisPraiseService implements IRedisPraiseService {
    private static final Logger log = LoggerFactory.getLogger(RedisPraiseService.class);

    private static final String KEY_BLOG = "RedisPraiseBlogMap";
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private PraiseMapper praiseMapper;
    @Autowired
    IPraiseService praiseService;

    @Override
    public void cachePraiseBlog(Integer blogId, Integer userId, Integer status) throws InterruptedException {
        final String lockName = "RedissonPraiseBlogLock" + blogId + userId + status;
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (res) {
                log.info("博客点赞加分布式锁成功!");
                // 参数合法性校验
                if (blogId != null && userId != null && status != null) {
                    final String key = blogId + ":" + userId;
                    RMap<String, Integer> praiseMap = redissonClient.getMap(KEY_BLOG);
                    if (status == 1) {
                        // 点赞
                        praiseMap.putIfAbsent(key, userId);
                    } else if (status == 0) {
                        praiseMap.remove(key);
                    }
                }
            }
        } finally {
            if (lock != null) {
                lock.forceUnlock();
            }
        }
    }

    @Override
    public Long getCacheTotalBlog(Integer blogId) {
        Long result = 0L;
        if (blogId != null) {
            RMap<String, Integer> praiseMap = redissonClient.getMap(KEY_BLOG);
            // 这个resultMap才是真正的 praiseMap.putIfAbsent(key, userId);   key = blogId + ":" + userId;
            Map<String, Integer> resultMap = praiseMap.readAllMap();
            if (resultMap != null) {
                log.info("从缓存中查询点赞的总数");
                Set<String> keySet = resultMap.keySet();
                Integer bId;
                for (String key : keySet) {
                    String[] arr = key.split(":");
                    if (arr.length > 0) {
                        bId = Integer.valueOf(arr[0]);
                        if (blogId.equals(bId)) {
                            result += 1;
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<PraiseRankDto> getBlogPraiseRank() {
        //定义用于缓存排行榜的Key
        final String key="praiseRankListKey";
        //获取Redisson的列表组件RList实例
        RList<PraiseRankDto> rList=redissonClient.getList(key);
        //获取缓存中最新的排行榜
        return rList.readAll();
    }

    @Override
    public void rankBlogPraise() {
        //定义用于缓存排行榜的Key
        final String key="praiseRankListKey";
        //根据数据库查询语句得到已经排好序的博客实体对象列表
        List<PraiseRankDto> list=praiseMapper.getPraiseRank();
        //判断列表中是否有数据
        if (list!=null && list.size()>0){
            //获取Redisson的列表组件RList实例
            RList<PraiseRankDto> rList=redissonClient.getList(key);
            //先清空缓存中的列表数据
            rList.clear();
            //将得到的最新的排行榜更新至缓存中
            rList.addAll(list);
        }
    }
}
