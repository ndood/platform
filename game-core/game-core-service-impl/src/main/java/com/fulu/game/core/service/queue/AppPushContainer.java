package com.fulu.game.core.service.queue;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.fulu.game.core.entity.vo.AppPushMsgVO;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
@Component
public class AppPushContainer extends RedisTaskContainer {

    private static final String APP_PUSH_QUEQUE = "app:push:queue";

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    private static int runTaskThreadNum = 1;

    //使用一个统一维护的线程池来管理隔离线程
    protected static ExecutorService es = Executors.newFixedThreadPool(runTaskThreadNum);

    private RedisConsumer redisConsumer;

    @PostConstruct
    private void init() {
        redisQueue = new RedisQueue<AppPushMsgVO>(APP_PUSH_QUEQUE, redisOpenService);
        if (!configProperties.getQueue().isMiniappPush()) {
            log.info("无需开启APP推送队列消费线程");
            es.shutdown();
            return;
        }
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

    private void process(AppPushMsgVO appPushMsgVO) {
        pushMsg(appPushMsgVO.getTitle(), appPushMsgVO.getAlert(), appPushMsgVO.getExtras(), appPushMsgVO.getUserIds());
    }

    /**
     * app推送消息
     *
     * @param title
     * @param alert
     * @param userIds
     * @param extras
     */
    public void pushMsg(String title, String alert, Map<String, String> extras, Integer... userIds) {
        if (userIds.length == 0) {
            push(title, alert, extras, null);
            return;
        }
        String[] strUserIds = new String[userIds.length];
        for (int i = 0; i < userIds.length; i++) {
            strUserIds[i] = String.valueOf(userIds[i]);
        }
        push(title, alert, extras, strUserIds);
    }

    /**
     * app推送方法
     *
     * @param title
     * @param alert
     * @param extras
     * @param userIds
     */
    private void push(String title, String alert, Map<String, String> extras, String[] userIds) {
        JPushClient jpushClient = new JPushClient(configProperties.getJpush().getAppSecret(), configProperties.getJpush().getAppKey());
        Audience audience = userIds == null ? Audience.all() : Audience.alias(userIds);
        PushPayload payload = buildPushPayload(title, alert, extras, audience);
        try {
            PushResult result = jpushClient.sendPush(payload);
            log.info("Got result - " + result);
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
            log.error("Sendno: " + payload.getSendno());
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            log.info("Msg ID: " + e.getMsgId());
            log.error("Sendno: " + payload.getSendno());
        }
    }


    /**
     * 构建PushPayload对象
     *
     * @param title
     * @param alert
     * @param extras
     * @param audience
     * @return
     */
    private PushPayload buildPushPayload(String title, String alert, Map<String, String> extras, Audience audience) {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(audience)

                .setNotification(Notification.newBuilder()
                        .setAlert(alert)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(title)
                                .addExtras(extras).build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtras(extras).build())
                        .build())
                .build();
        //todo 生产环境改成true
        payload.resetOptionsApnsProduction(false);

        return payload;
    }

    /**
     * 添加app推送对象到队列
     */
    public void add(AppPushMsgVO appPushMsgVO) {
        redisQueue.pushFromHead(appPushMsgVO);
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
