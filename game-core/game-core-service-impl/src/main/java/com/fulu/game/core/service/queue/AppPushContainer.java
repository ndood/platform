package com.fulu.game.core.service.queue;

import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.entity.vo.AppPushMsgVO;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.push.MobileAppPushServiceImpl;
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
public class AppPushContainer extends RedisTaskContainer {

    private static final String APP_PUSH_QUEQUE = "app:push:queue";

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @Autowired
    private MobileAppPushServiceImpl mobileAppPushService;

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
        redisQueue = new RedisQueue<AppPushMsgVO>(APP_PUSH_QUEQUE, redisOpenService);

        Consumer<AppPushMsgVO> consumer = (data) -> {
            // do something
            process(data);
        };

        //提交线程
        for (int i = 0; i < runTaskThreadNum; i++) {
            redisConsumer = new RedisConsumer<>(this, consumer);
            es.execute(redisConsumer);
        }
    }


    private void process(AppPushMsgVO appPushMsgVO){
        mobileAppPushService.pushMsg(appPushMsgVO);
    }

    /**
     * 添加app推送对象到队列
     */
    public void add(WechatFormid wechatFormid) {
        redisQueue.pushFromHead(wechatFormid);
    }

    @PreDestroy
    public void destroy() {
        log.info("app推送队列销毁");
        if (es != null) {
            es.shutdown();
        }
        if (redisConsumer != null) {
            redisConsumer.shutdown();
        }
    }
}
