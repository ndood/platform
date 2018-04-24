package com.fulu.game.core.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class RedisOpenServiceImpl {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final long time = 5 * 60;

    public String get(String key) {
        return redisTemplate.opsForValue().get(key).toString();
    }

    public void set(String key, String value) {
        set(key, value, time);
    }

    public void set(String key, String value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    public void hset(String key, String hash, String value) {
        hset(key, hash, value, time);
    }

    public void hset(String key, String hash, String value, long time) {
        redisTemplate.opsForHash().put(key, hash, value);
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    public void hset(String key, Map map) {
        hset(key, map, time);
    }

    public void hset(String key, Map map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }


}
