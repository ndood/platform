package com.fulu.game.core.service.queue;

import com.fulu.game.common.properties.Config;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisTaskContainer {

    @Autowired
    protected Config configProperties;

    @Autowired
    protected RedisOpenServiceImpl redisOpenService;

    //队列里边的数据泛型可以根据实际情况调整, 可以定义多个类似的队列
    protected RedisQueue redisQueue;

    public RedisQueue getRedisQueue() {
        return redisQueue;
    }
}
