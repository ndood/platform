package com.fulu.game.core.service.queue;

import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import org.springframework.data.redis.core.BoundListOperations;


public class RedisQueue<T> {

    private BoundListOperations<String, T> listOperations;

    private RedisOpenServiceImpl redisOpenService;
    private final String rawkey ;
    public RedisQueue(String key, RedisOpenServiceImpl redisOpenService) {
        rawkey =key;
        this.redisOpenService = redisOpenService;
        listOperations = redisOpenService.getListOps(key);
    }

    /**
     * blocking 一直阻塞直到队列里边有数据
     * remove and get last item from queue:BRPOP
     *
     * @return
     */
    public T takeFromTail(int timeout) throws InterruptedException {
        return redisOpenService.takeFromTail(rawkey,timeout);
    }

    public T takeFromTail() throws InterruptedException {
        return takeFromTail(0);
    }

    /**
     * 从队列的头，插入
     */
    public void pushFromHead(T value) {
        listOperations.leftPush(value);
    }

    public void pushFromTail(T value) {
        listOperations.rightPush(value);
    }

    /**
     * noblocking
     *
     * @return null if no item in queue
     */
    public T removeFromHead() {
        return listOperations.leftPop();
    }

    public T removeFromTail() {
        return listOperations.rightPop();
    }

    /**
     * blocking 一直阻塞直到队列里边有数据
     * remove and get first item from queue:BLPOP
     *
     * @return
     */
    public T takeFromHead(int timeout) throws InterruptedException {
        return redisOpenService.takeFromHead(rawkey,timeout);
    }

    public T takeFromHead() throws InterruptedException {
        return takeFromHead(0);
    }

}
