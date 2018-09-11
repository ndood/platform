package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.RedisKeyEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * redis操作封装类
 */
@Service
public class RedisOpenServiceImpl {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 默认存活时间30分钟
     */
    private static final long TIME = 30 * 60;


    private static final String LOCKED = "LOCKED";

    private static Lock lock = new ReentrantLock();//基于底层IO阻塞考虑

    private byte[] rawKey;

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
     * 添加一个BitSet
     *
     * @param key
     * @param val
     */
    public void bitSet(String key, long val, boolean flag) {
        redisTemplate.opsForValue().setBit(key, val, flag);
    }
    /**
     * 添加一个BitSet,默认为true
     *
     * @param key
     * @param val
     */
    public void bitSet(String key, long val) {
        bitSet(key, val, true);
    }

    /**
     * 判断bitSet值(Value是在bit中存在)
     *
     * @param key
     * @param val
     * @return
     */
    public Boolean getBitSet(String key, long val) {
        return redisTemplate.opsForValue().getBit(key, val);
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

    /**
     * redis锁
     *
     * @param key
     * @param expireTime
     */
    public boolean lock(String key, long expireTime) {
        if (redisTemplate.opsForValue().setIfAbsent(key, LOCKED)) {
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param key
     */
    public void unlock(String key) {
        // 释放锁
        redisTemplate.delete(key);
    }

    /**
     * 存储时间间隔
     *
     * @param key
     * @param time
     */
    public void setTimeInterval(String key, long time) {
        set(RedisKeyEnum.TIME_INTERVAL_KEY.generateKey(key), new Date().getTime() + "", time);
    }


    /**
     * 判断时间间隔
     *
     * @param key
     * @return
     */
    public Boolean isTimeIntervalInside(String key) {
        return hasKey(RedisKeyEnum.TIME_INTERVAL_KEY.generateKey(key));
    }

    /**
     * 获取listOps来操作list
     * @param key
     * @return
     */
    public <T, S> BoundListOperations<T, S> getListOps(String key) {
        rawKey = redisTemplate.getKeySerializer().serialize(key);
        return redisTemplate.boundListOps(key);
    }

    /**
     * blocking 一直阻塞直到队列里边有数据
     * remove and get last item from queue:BRPOP
     * @return
     */
    public <T> T takeFromTail(int timeout) throws InterruptedException{
        lock.lockInterruptibly();
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        RedisConnection connection = connectionFactory.getConnection();
        try{
            List<byte[]> results = connection.bRPop(timeout, rawKey);
            if(CollectionUtils.isEmpty(results)){
                return null;
            }
            return (T)redisTemplate.getValueSerializer().deserialize(results.get(1));
        }finally{
            lock.unlock();
            RedisConnectionUtils.releaseConnection(connection, connectionFactory);
        }
    }

    /**
     * blocking 一直阻塞直到队列里边有数据
     * remove and get first item from queue:BLPOP
     * @return
     */
    public <T> T takeFromHead(int timeout) throws InterruptedException{
        lock.lockInterruptibly();
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        RedisConnection connection = connectionFactory.getConnection();
        try{
            List<byte[]> results = connection.bLPop(timeout, rawKey);
            if(CollectionUtils.isEmpty(results)){
                return null;
            }
            return (T)redisTemplate.getValueSerializer().deserialize(results.get(1));
        }finally{
            lock.unlock();
            RedisConnectionUtils.releaseConnection(connection, connectionFactory);
        }
    }

}
