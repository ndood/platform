package com.fulu.game.app.service.impl;

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
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.properties.Config;
import com.fulu.game.core.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class MobileAppPushServiceImpl implements PushService {


    private final Config configProperties;

    @Autowired
    public MobileAppPushServiceImpl(Config configProperties) {
        this.configProperties = configProperties;
    }


    /**
     * 推送全体用户
     *
     * @param title
     * @param alert
     * @param extras
     */
    public void pushMsg(String title, String alert, Map<String, String> extras) {
        JPushClient jpushClient = new JPushClient(configProperties.getJpush().getAppSecret(), configProperties.getJpush().getAppKey());
        PushPayload payload = buildPushPayload(title, alert, extras, Audience.all());
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
     * 指定用户ID推送消息
     *
     * @param title
     * @param alert
     * @param userIds
     * @param extras
     */
    public void pushMsg(String title, String alert, Map<String, String> extras, Integer[] userIds) {
        if (userIds == null) {
            throw new ServiceErrorException("消息推送Id不能为空");
        }
        String[] strUserIds = new String[userIds.length];
        for (int i = 0; i < userIds.length; i++) {
            strUserIds[i] = String.valueOf(userIds[i]);
        }
        JPushClient jpushClient = new JPushClient(configProperties.getJpush().getAppSecret(), configProperties.getJpush().getAppKey());
        PushPayload payload = buildPushPayload(title, alert, extras, Audience.alias(strUserIds));
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

        return payload;
    }

}
