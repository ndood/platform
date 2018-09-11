package com.fulu.game.core.service.queue;

import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.service.WechatFormidService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
@Component
public class FormIdContainer extends RedisTaskContainer {

    private static final String MINI_APP_FORMID_QUEQUE = "miniapp:formid:queue";

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @Autowired
    private WechatFormidService wechatFormidService;

    private static int runTaskThreadNum = 1;

    //使用一个统一维护的线程池来管理隔离线程
    protected static ExecutorService es = Executors.newFixedThreadPool(runTaskThreadNum);

    private RedisConsumer redisConsumer;

    @PostConstruct
    private void init() {
        if (!configProperties.getQueue().isFormId()) {
            log.info("无需开启小程序formId收集队列线程");
            es.shutdown();
            return;
        }
        redisQueue = new RedisQueue<WechatFormid>(MINI_APP_FORMID_QUEQUE, redisOpenService);

        Consumer<WechatFormid> consumer = (data) -> {
            process(data);
        };

        //提交线程
        for (int i = 0; i < runTaskThreadNum; i++) {
            redisConsumer = new RedisConsumer<>(this, consumer);
            es.execute(redisConsumer);
        }
    }

    /**
     * 添加收集到的formid到数据库
     */
    private void process(WechatFormid formid) {
        wechatFormidService.create(formid);
    }

    /**
     * 添加formid到队列
     */
    public void add(WechatFormid wechatFormid) {
        redisQueue.pushFromHead(wechatFormid);
    }

    @PreDestroy
    public void destroy() {
        log.info("formId收集队列销毁");
        if (es != null) {
            es.shutdown();
        }
        if (redisConsumer != null) {
            redisConsumer.shutdown();
        }
    }

}
