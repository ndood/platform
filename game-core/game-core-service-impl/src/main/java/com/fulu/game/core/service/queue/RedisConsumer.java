package com.fulu.game.core.service.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class RedisConsumer<T> extends Thread {


    private RedisTaskContainer container;

    private Consumer<T> consumer;

    private AtomicBoolean run = new AtomicBoolean();

    public RedisConsumer(RedisTaskContainer container, Consumer<T> consumer) {
        run.set(true);
        this.container = container;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try {
            do {
                Thread.sleep(1000);
                RedisQueue<T> queue = container.getRedisQueue();//.takeFromTail();
                T value = queue.takeFromTail();
                //逐个执行
                if (value != null) {
                    try {
                        consumer.accept(value);
                    } catch (Exception e) {
                        log.error("调用失败", e);
                    }
                }
            } while (run.get());
        } catch (Exception e) {
            log.error("轮循线程异常退出", e);
        }
    }

    public void shutdown() {
        run.set(false);
    }

    ;
}
