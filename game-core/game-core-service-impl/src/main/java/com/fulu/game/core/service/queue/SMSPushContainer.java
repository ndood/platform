package com.fulu.game.core.service.queue;

import com.fulu.game.common.enums.SMSTemplateEnum;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.core.entity.vo.AppPushMsgVO;
import com.fulu.game.core.entity.vo.SMSVO;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
@Component
public class SMSPushContainer extends RedisTaskContainer {

    private static final String SMS_PUSH_QUEQUE = "sms:push:queue";

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    private static int runTaskThreadNum = 1;

    //使用一个统一维护的线程池来管理隔离线程
    protected static ExecutorService es = Executors.newFixedThreadPool(runTaskThreadNum);

    private RedisConsumer redisConsumer;

    @PostConstruct
    private void init() {
        redisQueue = new RedisQueue<AppPushMsgVO>(SMS_PUSH_QUEQUE, redisOpenService);
        if (!configProperties.getQueue().isMiniappPush()) {
            log.info("无需开启SMS推送队列消费线程");
            es.shutdown();
            return;
        }
        Consumer<SMSVO> consumer = (data) -> {
            // do something
            process(data);
        };

        //提交线程
        for (int i = 0; i < runTaskThreadNum; i++) {
            redisConsumer = new RedisConsumer<>(this, consumer);
            es.execute(redisConsumer);
        }
    }

    private void process(SMSVO smsvo) {
        SMSUtil.sendSMS(smsvo.getMobile(), smsvo.getTemplateEnum(), smsvo.getParams());
    }

    /**
     * 添加app推送对象到队列
     */
    public void add(SMSVO smsvo) {
        redisQueue.pushFromHead(smsvo);
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