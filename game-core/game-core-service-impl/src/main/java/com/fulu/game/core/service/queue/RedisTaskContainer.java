package com.fulu.game.core.service.queue;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Component
public class RedisTaskContainer {


    public static String ORDER_SEND_REDIS_QUEQUE = "order:send:redis:queue";
    private static int runTaskThreadNum = 2;//Runtime.getRuntime().availableProcessors()

    //使用一个统一维护的线程池来管理隔离线程
    private static ExecutorService es = Executors.newFixedThreadPool(runTaskThreadNum);

    @Resource
    private RedisTemplate redisTemplate;
    //队列里边的数据泛型可以根据实际情况调整, 可以定义多个类似的队列
    private RedisQueue<Msg> redisQueue = null;

    @PostConstruct
    private void init() {
        redisQueue = new RedisQueue(redisTemplate, ORDER_SEND_REDIS_QUEQUE);

        Consumer<Msg> consumer = (data) -> {
            // do something
            System.out.println(data);
        };

        //提交线程
        for (int i = 0; i < runTaskThreadNum; i++) {
            es.execute(new OrderSendRedisConsumer(this, consumer));
        }
    }

    public RedisQueue<Msg> getRedisQueue() {
        return redisQueue;
    }
}
