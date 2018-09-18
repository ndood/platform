package com.fulu.game.core.service.impl.push;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
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
import com.alibaba.fastjson.JSONArray;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.WechatTemplateIdEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.properties.Config;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.WxMaTemplateMessageVO;
import com.fulu.game.core.entity.vo.WechatFormidVO;
import com.fulu.game.core.service.PushService;
import com.fulu.game.core.service.UserInfoAuthService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.WechatFormidService;
import com.fulu.game.core.service.queue.MiniAppPushContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PushServiceImpl implements PushService {

    @Autowired
    private MiniAppPushContainer miniAppPushContainer;
    @Autowired
    private UserService userService;
    @Autowired
    private WechatFormidService wechatFormidService;
    @Autowired
    private Config configProperties;
    @Qualifier(value = "userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthService userInfoAuthService;

    /**
     * 通用模板推送消息
     * 只有小程序才会用到的推送，应该放miniappPushServiceImpl，但是因为admin也会调用，所以放pushServiceImpl下
     *
     * @param platform
     * @param userId
     * @param templateIdEnum
     * @param wechatPage
     * @param content
     * @param replaces
     */
    protected void pushWechatTemplateMsg(int platform, int userId, WechatTemplateIdEnum templateIdEnum, String wechatPage, String content, String... replaces) {
        if (replaces != null && replaces.length > 0) {
            content = StrUtil.format(content, replaces);
        }
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList;
        switch (templateIdEnum) {
            case PLAY_LEAVE_MSG:
                dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", content), new WxMaTemplateMessage.Data("keyword2", date));
                break;
            default:
                dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", content), new WxMaTemplateMessage.Data("keyword2", date));
        }

        addTemplateMsg2Queue(platform, null, Collections.singletonList(userId), wechatPage, templateIdEnum, dataList);
    }

    /**
     * 推送“服务进度通知”
     *
     * @param wechatTemplateMsgEnum
     * @param replaces
     * @return
     */
    protected void pushServiceProcessMsg(int platform,
                                         int userId,
                                         Order order,
                                         WechatTemplateIdEnum wechatTemplateIdEnum,
                                         WechatTemplateMsgEnum wechatTemplateMsgEnum,
                                         String... replaces) {
        String content = wechatTemplateMsgEnum.getContent();
        if (replaces != null && replaces.length > 0) {
            content = StrUtil.format(content, replaces);
        }
        String date = DateUtil.format(DateUtil.date(), "yyyy年MM月dd日 HH:mm");
        String serviceUserNickName = "";
        User serviceUser = userService.findById(order.getServiceUserId());
        if (serviceUser != null) {
            serviceUserNickName = serviceUser.getNickname();
        }

        List<WxMaTemplateMessage.Data> dataList = CollectionUtil.newArrayList(
                //服务进度（订单状态）
                new WxMaTemplateMessage.Data("keyword1", OrderStatusEnum.getMsgByStatus(order.getStatus())),
                //订单编号
                new WxMaTemplateMessage.Data("keyword2", order.getOrderNo()),
                //订单金额
                new WxMaTemplateMessage.Data("keyword3", "￥" + order.getTotalMoney().toPlainString()),
                //通知时间
                new WxMaTemplateMessage.Data("keyword4", date),
                //服务人员（陪玩师姓名）
                new WxMaTemplateMessage.Data("keyword5", serviceUserNickName),
                //备注（消息模板内容）
                new WxMaTemplateMessage.Data("keyword6", content));
        if (PlatformEcoEnum.PLAY.getType().equals(platform)) {
            addTemplateMsg2Queue(platform, null, Collections.singletonList(userId), wechatTemplateMsgEnum.getPage().getPlayPagePath(), wechatTemplateIdEnum, dataList);
        } else if (PlatformEcoEnum.POINT.getType().equals(platform)) {
            addTemplateMsg2Queue(platform, null, Collections.singletonList(userId), wechatTemplateMsgEnum.getPage().getPointPagePath(), wechatTemplateIdEnum, dataList);
        }
    }

    /**
     * 批量写入推送模板消息
     *
     * @param pushId
     * @param userIds
     * @param page
     * @param wechatTemplateEnum
     * @param dataList
     */
    public synchronized void addTemplateMsg2Queue(int platform,
                                                  Integer pushId,
                                                  List<Integer> userIds,
                                                  String page,
                                                  WechatTemplateIdEnum wechatTemplateEnum,
                                                  List<WxMaTemplateMessage.Data> dataList) {
        //删除表里面过期的formId
        long startTime = System.currentTimeMillis();
        Date date = DateUtil.offset(new Date(), DateField.HOUR, (-24 * 7) + 1);
        wechatFormidService.deleteByExpireTime(date);
        long endTime = System.currentTimeMillis();
        log.info("pushTask:{}执行wechatFormidService.deleteByExpireTime方法耗时:{}", pushId, endTime - startTime);
        for (int i = 0; ; i = +1000) {
            List<WechatFormidVO> wechatFormidVOS = null;
            try {
                long findStartTime = System.currentTimeMillis();
                List<User> users = userService.findByUserIds(userIds, Boolean.TRUE);
                wechatFormidVOS = wechatFormidService.findByUserIds(platform, userIds, i, 1000);
                long findEndTime = System.currentTimeMillis();
                log.info("pushTask:{}执行wechatFormidService.findByUserIds:{}", pushId, findEndTime - findStartTime);
                if (wechatFormidVOS.isEmpty()) {
                    break;
                }
                List<String> formIds = new ArrayList<>();
                Map<Integer, String> userFormIds = new HashMap<>();
                //发送微信模板消息
                for (WechatFormidVO wechatFormidVO : wechatFormidVOS) {
                    WxMaTemplateMessageVO vo = WxMaTemplateMessageVO.builder()
                            .templateId(wechatTemplateEnum.getTemplateId())
                            .toUser(wechatFormidVO.getOpenId())
                            .page(page)
                            .formId(wechatFormidVO.getFormId())
                            .dataJson(JSONArray.toJSONString(dataList))
                            .platform(platform)
                            .pushId(pushId)
                            .build();
                    miniAppPushContainer.add(vo);
                    formIds.add(wechatFormidVO.getFormId());
                    userFormIds.put(wechatFormidVO.getUserId(), wechatFormidVO.getFormId());
                }
                //TODO 补发短信的逻辑有问题
                //没有有效formId的user进行短信补发
                sendSMSIfFormIdInVaild(userFormIds, users, dataList);
                //删除已经发过的formId
                if (formIds.size() > 0) {
                    long delStartTime = System.currentTimeMillis();
                    wechatFormidService.deleteFormIds(formIds.toArray(new String[]{}));
                    long delEndTime = System.currentTimeMillis();
                    log.info("pushTask:{}执行wechatFormidService.deleteFormIds方法耗时:{}", pushId, delEndTime - delStartTime);
                }
            } catch (Exception e) {
                log.error("批量写入推送模板消息异常wechatFormidVOS:{}", wechatFormidVOS);
                log.error("批量写入推送模板消息异常", e);
            }

        }
    }

    /**
     * 根据userId获取formID
     *
     * @param platform 平台
     * @param userId   用户id
     * @return vo
     */
    private WechatFormidVO findFormidVOByUserId(int platform, Integer userId) {
        Date date = DateUtil.offset(new Date(), DateField.HOUR, (-24 * 7) + 1);
        wechatFormidService.deleteByExpireTime(date);

        List<Integer> userIds = new ArrayList<>();
        userIds.add(userId);
        List<WechatFormidVO> wechatFormidVOS = wechatFormidService.findByUserIds(platform, userIds, 0, 1);
        if (CollectionUtil.isEmpty(wechatFormidVOS)) {
            return null;
        }
        return wechatFormidVOS.get(0);
    }

    /**
     * 如果formid无效，补发短信
     *
     * @param userFormId
     * @param userList
     * @param dataList
     */
    private void sendSMSIfFormIdInVaild(Map<Integer, String> userFormId, List<User> userList, List<WxMaTemplateMessage.Data> dataList) {
        String content = null;
        if (CollectionUtil.isEmpty(dataList)) {
            return;
        }
        for (WxMaTemplateMessage.Data data : dataList) {
            if ("keyword1".equals(data.getName())) {
                content = data.getValue();
            }
        }
        if (StringUtils.isEmpty(content) || CollectionUtil.isEmpty(userList)) {
            return;
        }
        for (User user : userList) {
            String formId = userFormId.get(user.getId());
            if (StringUtils.isEmpty(formId)) {
                log.error("user或者formId为null无法给用户推送消息user:{};content:{};formId:{}", user, content, formId);
                if (StringUtils.isNotEmpty(user.getMobile())) {
                    UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(user.getId());
                    boolean vestFlag = false;
                    if (userInfoAuth != null) {
                        vestFlag = userInfoAuth.getVestFlag();
                    }

                    if (!vestFlag) {
                        Boolean flag = SMSUtil.sendLeaveInform(user.getMobile(), content, Constant.WEIXN_JUMP_URL);
                        if (!flag) {
                            log.error("留言通知发送短信失败:user.getMobile:{};content:{};", user.getMobile(), content);
                        } else {
                            log.info("留言通知发送短信成功:user.getMobile:{};content:{};", user.getMobile(), content);
                        }
                    }
                }
            }
            return;
        }
    }


    /**
     * 推送全体用户
     *
     * @param title
     * @param alert
     * @param extras
     */
    protected void pushMsg(String title, String alert, Map<String, String> extras) {
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
    protected void pushMsg(String title, String alert, Map<String, String> extras, Integer[] userIds) {
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

        return payload;
    }


}
