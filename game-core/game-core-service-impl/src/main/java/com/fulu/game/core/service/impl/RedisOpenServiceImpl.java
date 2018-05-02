package com.fulu.game.core.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis操作封装类
 */
@Service
public class RedisOpenServiceImpl {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 默认存活时间5分钟
     */
    private static final long TIME = 30 * 60;

    /**
     * 获取某个key的值
     *
     * @param key
     * @return
     */
    public String get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return (value != null) ? value.toString() : null;
    }

    /**
     * 设置某个key的值
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        set(key, value, TIME);
    }

    /**
     * 设置某个key的值及保存时间
     *
     * @param key
     * @param value
     * @param time
     */
    public void set(String key, String value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 根据key设置某个hash的值
     *
     * @param key
     * @param hash
     * @param value
     */
    public void hset(String key, String hash, Object value) {
        hset(key, hash, value, TIME);
    }

    /**
     * 根据key设置某个hash的值及存活时间
     *
     * @param key
     * @param hash
     * @param value
     * @param time
     */
    public void hset(String key, String hash, Object value, long time) {
        redisTemplate.opsForHash().put(key, hash, value);
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 根据key设置hashtable的值
     *
     * @param key
     * @throws Exception
     */
    public void hset(String key, Map<String, Object> map) {
        hset(key, map, TIME);
    }

    /**
     * 根据key设置hashtable的值
     *
     * @param key
     * @param time
     * @throws Exception
     */
    public void hset(String key, Map<String, Object> map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 根据key获取某个hash的值
     *
     * @param key
     * @param hash
     * @return
     */
    public String hget(String key, String hash) {
        Object value = redisTemplate.opsForHash().get(key, hash);
        return (value != null) ? value.toString() : null;
    }

    /**
     * 根据key获取整个hashtable
     *
     * @param key
     * @return
     */
    public Map<String, Object> hget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 根据key删除整个hashtable
     *
     * @param key
     * @return
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }


}
