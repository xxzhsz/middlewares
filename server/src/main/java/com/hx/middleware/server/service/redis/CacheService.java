package com.hx.middleware.server.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hx.middleware.model.entity.Item;
import com.hx.middleware.model.mapper.ItemMapper;
import com.hx.middleware.server.controller.redis.CachePassController;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author jxlgcmh
 * @date 2020-02-07 11:30
 * @description
 */
@Service
public class CacheService {
    private static final Logger log = LoggerFactory.getLogger(CachePassController.class);

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    //key前缀
    private static final String KEY_PREFIX = "item:";

    public Item getItemInfo(String itemCode) throws IOException {
        Item item = null;
        final String key = KEY_PREFIX + itemCode;
        // redis中存在这个key
        if (redisTemplate.hasKey(key)) {
            Object object = redisTemplate.opsForValue().get(key);
            // 如果获取的对象不为空
            if (object != null && !Strings.isNullOrEmpty(object.toString())) {
                 item = objectMapper.readValue(object.toString(), Item.class);
                log.info("缓存存中获取到的商品信息为:{}",item);
            }
        }else {
            log.info("缓存存中没有对应的商品信息为:{}",itemCode);
            //缓存没有找到   从数据库中找
             item = itemMapper.selectByCode(itemCode);
            if (item != null) {
                //数据库中查找到的也不为空，写入缓存
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(item));
                log.info("将商品信息写入缓存:{}",itemCode);
            } else {
                // 这里是关键步骤
                redisTemplate.opsForValue().set(key, "", 30L, TimeUnit.MINUTES);
            }
        }
        return item;
    }
}
