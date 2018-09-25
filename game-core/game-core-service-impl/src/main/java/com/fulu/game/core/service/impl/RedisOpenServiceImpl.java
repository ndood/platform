package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.RedisKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * redis操作封装类
 */
@Service
@Slf4j
public class RedisOpenServiceImpl {

    /**
     * 默认存活时间30分钟
     */
    private static final long TIME = 30 * 60;
    private static final String LOCKED = "LOCKED";
    private static Lock lock = new ReentrantLock();//基于底层IO阻塞考虑
    @Autowired
    private RedisTemplate redisTemplate;


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
     * 统计bit位为1的总数
     * @param key
     */
    public Long bitCount(final String key) {
        return (Long) redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                result = connection.bitCount(key.getBytes());
                return result;
            }
        });
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
     * 设置某个key的值
     *
     * @param key
     * @param value
     * @param isPerpetual 是否永久保存（true：是；false：否）
     */
    public void set(String key, String value, boolean isPerpetual) {
        if(isPerpetual){
            redisTemplate.opsForValue().set(key, value);
        } else {
            set(key, value);
        }
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
     * 根据key设置某个hash的值
     *
     * @param key
     * @param hash
     * @param value
     * @param isPerpetual 是否永久保存（true：是；false：否）
     */
    public void hset(String key, String hash, Object value, boolean isPerpetual) {
        if (isPerpetual) {
            redisTemplate.opsForHash().put(key, hash, value);
        } else {
            hset(key, hash, value, TIME);
        }
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
     * @param map
     * @param isPerpetual 是否永久保存（true：是；false：否）
     */
    public void hset(String key, Map<String, Object> map, boolean isPerpetual) {
        if (isPerpetual) {
            redisTemplate.opsForHash().putAll(key, map);
        } else {
            hset(key, map, TIME);
        }
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
     *
     * @param key
     * @return
     */
    public <T, S> BoundListOperations<T, S> getListOps(String key) {
        return redisTemplate.boundListOps(key);
    }

    /**
     * blocking 一直阻塞直到队列里边有数据
     * remove and get last item from queue:BRPOP
     *
     * @return
     */
    public <T> T takeFromTail(String key, int timeout) throws InterruptedException {
//        lock.lockInterruptibly();
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        RedisConnection connection = connectionFactory.getConnection();
        try {
            byte[] rawKey = redisTemplate.getKeySerializer().serialize(key);
            List<byte[]> results = connection.bRPop(timeout, rawKey);
            if (CollectionUtils.isEmpty(results)) {
                return null;
            }
            return (T) redisTemplate.getValueSerializer().deserialize(results.get(1));
        } catch (Exception e) {
            log.error("获取队列信息异常:", e);
            return null;
        } finally {
//            lock.unlock();
            RedisConnectionUtils.releaseConnection(connection, connectionFactory);
        }
    }

    /**
     * blocking 一直阻塞直到队列里边有数据
     * remove and get first item from queue:BLPOP
     *
     * @return
     */
    public <T> T takeFromHead(String key, int timeout) throws InterruptedException {
        //        lock.lockInterruptibly();
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        RedisConnection connection = connectionFactory.getConnection();
        try {
            byte[] rawKey = redisTemplate.getKeySerializer().serialize(key);
            List<byte[]> results = connection.bLPop(timeout, rawKey);
            if (CollectionUtils.isEmpty(results)) {
                return null;
            }
            return (T) redisTemplate.getValueSerializer().deserialize(results.get(1));
        } catch (Exception e) {
            log.error("获取队列信息异常:", e);
            return null;
        } finally {
//            lock.unlock();
            RedisConnectionUtils.releaseConnection(connection, connectionFactory);
        }
    }



    /**
     * 自增
     * @param key
     * @return
     */
    public long incr(String key) {
        return incr( key, 1);
    }


    /**
     * 递增
     * @param key
     * @param delta 递增因子，必须大于0
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自减
     * @param key 键
     * @return
     */
    public long decr(String key){
        return decr( key, 1);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }


    /**
     * 获取Integer类型的值
     * @param key
     * @return
     */
    public Integer getInteger(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return (value != null) ? (Integer) value : 0;
    }

    /**
     * 通配符查找redis的key
     *
     * @param parttern
     * @return
     */
    public Set<String> keys(String parttern) {
        return redisTemplate.keys(parttern);
    }

}
