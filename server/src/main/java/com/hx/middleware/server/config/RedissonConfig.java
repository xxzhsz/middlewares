package com.hx.middleware.server.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author jxlgcmh
 * @date 2020-02-14 21:19
 * @description
 */
@Configuration
public class RedissonConfig {
    @Autowired
    private Environment env;


    @Bean
    public RedissonClient redissonClient() {
        Config config =new Config();
        config.useSingleServer().setAddress(env.getProperty("redisson.host")).setKeepAlive(true);
        return Redisson.create(config);
    }

}
