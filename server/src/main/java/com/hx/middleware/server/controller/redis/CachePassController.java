package com.hx.middleware.server.controller.redis;

import com.hx.middleware.server.service.redis.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jxlgcmh
 * @date 2020-02-07 11:28
 * @description
 */
@RestController
@RequestMapping("cache/pass")
public class CachePassController {
    private static final Logger log = LoggerFactory.getLogger(CachePassController.class);


    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = "/item/info",method = RequestMethod.GET)
    public Map<String, Object> getItem(@RequestParam String itemCode) {
        Map<String, Object> result = new HashMap<>();
        result.put("code",200);
        result.put("msg","success");
        try {
            result.put("data",cacheService.getItemInfo(itemCode));
        } catch (IOException e) {
            result.put("msg","failure");
            result.put("data",null);
        }
        return result;
    }
}
