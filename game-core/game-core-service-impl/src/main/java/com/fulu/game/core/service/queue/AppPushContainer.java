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
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.SMSTemplateEnum;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.vo.AppPushMsgVO;
import com.fulu.game.core.entity.vo.SMSVO;
import com.fulu.game.core.service.UserInfoAuthService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.push.SMSPushServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Autowired
    private UserService userService;
    @Autowired
    private SMSPushServiceImpl smsPushService;

    private static final int PAGESIZE = 100;

    @Qualifier(value = "userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthService userInfoAuthService;

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

    /**
     * app推送消息
     *
     * @param appPushMsgVO
     */
    private void process(AppPushMsgVO appPushMsgVO) {
        String title = appPushMsgVO.getTitle();
        String alert = appPushMsgVO.getAlert();
        Map<String, String> extras = appPushMsgVO.getExtras();
        Integer[] userIds = appPushMsgVO.getUserIds();
        if (userIds.length == 0) {
            push(title, alert, extras, null);
            return;
        }
        //最终要推送的userIds
        List<String> targetUids = new ArrayList<>();

        int pageNum = 0;
        List<UserInfoAuth> userInfoAuths;
        do {
            userInfoAuths = userInfoAuthService.findByUserIds(Arrays.asList(userIds), pageNum, PAGESIZE);
            userInfoAuths.forEach(userInfoAuth -> {
                if (userInfoAuth != null && userInfoAuth.getVestFlag()) {
                    for (Integer userId : userIds) {
                        if (!userInfoAuth.getUserId().equals(userId)) {
                            targetUids.add(String.valueOf(userId));
                        }
                    }
                    log.info("马甲用户，不发通知");
                }
            });
            push(title, alert, extras, targetUids.toArray(new String[targetUids.size()]));
        } while (CollectionUtils.isNotEmpty(userInfoAuths));
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
        Audience audience;
        if (userIds == null || userIds.length == 0) {
            audience = Audience.all();
            log.info("app全部用户推送");
        } else {
            audience = Audience.alias(userIds);
            log.info("app部分用户推送：{}", userIds);
        }
        PushPayload payload = buildPushPayload(title, alert, extras, audience);
        try {
            PushResult result = jpushClient.sendPush(payload);
            log.info("Got result - " + result);
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
            log.error("Sendno: " + payload.getSendno());
            sendSMSIfFail(alert, userIds);
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            log.info("Msg ID: " + e.getMsgId());
            log.error("Sendno: " + payload.getSendno());
            sendSMSIfFail(alert, userIds);
        }
    }

    /**
     * app推送失败，补发短信
     *
     * @param alert   内容
     * @param userIds 发送对象
     */
    private void sendSMSIfFail(String alert, String[] userIds) {
        int pagenum = 0;
        List<Integer> userIdd = new ArrayList<>();
        for (String userId : userIds) {
            if (NumberUtils.isDigits(userId)) {
                userIdd.add(Integer.valueOf(userId));
            } else {
                log.info("app推送失败补发短信，userId不是数字：{}", userId);
            }
        }
        List<User> users;
        do {
            users = userService.findByUserIds(userIdd, Boolean.TRUE, pagenum, PAGESIZE);
            for (User user : users) {
                String[] params = {alert, Constant.APP_JUMP_URL};
                SMSVO smsvo = new SMSVO(user.getMobile(), SMSTemplateEnum.SMS_REMIND, params);
                smsPushService.pushMsg(smsvo);
            }
        } while (CollectionUtils.isNotEmpty(users));
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
