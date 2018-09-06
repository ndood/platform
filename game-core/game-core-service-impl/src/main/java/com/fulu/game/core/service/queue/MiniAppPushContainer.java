package com.fulu.game.core.service.queue;

import com.fulu.game.core.entity.WxMaTemplateMessageVO;
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
public class MiniAppPushContainer extends RedisTaskContainer {

    private static final String MINI_APP_PUSH_QUEQUE = "miniapp:push:queue";

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    private static int runTaskThreadNum = 1;

    //使用一个统一维护的线程池来管理隔离线程
    protected static ExecutorService es = Executors.newFixedThreadPool(runTaskThreadNum);

    private RedisConsumer redisConsumer;

    @PostConstruct
    private void init() {
        if (!configProperties.getQueue().isMiniappPush()) {
            log.info("无需开启小程序推送队列线程");
            es.shutdown();
            return;
        }
        redisQueue = new RedisQueue<WxMaTemplateMessageVO>(MINI_APP_PUSH_QUEQUE, redisOpenService);

        Consumer<WxMaTemplateMessageVO> consumer = (data) -> {
            // do something
            System.out.println(data);
        };

        //提交线程
        for (int i = 0; i < runTaskThreadNum; i++) {
            redisConsumer = new RedisConsumer<>(this, consumer);
            es.execute(redisConsumer);
        }
    }

    /**
     * 添加小程序推送元素到队列
     */
    public void add(WxMaTemplateMessageVO wxMaTemplateMessageVO) {
        redisQueue.pushFromHead(wxMaTemplateMessageVO);
    }

    @PreDestroy
    public void destroy() {
        log.info("小程序推送队列销毁");
        if (es != null) {
            es.shutdown();
        }
        if (redisConsumer != null) {
            redisConsumer.shutdown();
        }
    }
}
