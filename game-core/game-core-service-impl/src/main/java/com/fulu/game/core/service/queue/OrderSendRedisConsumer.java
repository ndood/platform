package com.fulu.game.core.service.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class OrderSendRedisConsumer extends Thread {


    private RedisTaskContainer container;

    private Consumer<Msg> consumer;

    public OrderSendRedisConsumer(RedisTaskContainer container, Consumer<Msg> consumer) {
        this.container = container;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(30000);
                Msg value = container.getRedisQueue().takeFromTail();//cast exception? you should check.
                //逐个执行
                if (value != null) {
                    try {
                        consumer.accept(value);
                    } catch (Exception e) {
                        log.error("调用失败", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("轮循线程异常退出", e);
        }
    }
}
