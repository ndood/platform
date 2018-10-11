package com.fulu.game.core.service.impl.push;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.WxMaTemplateMessageVO;
import com.fulu.game.core.entity.vo.WechatFormidVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.aop.UserScore;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.queue.MiniAppPushContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public abstract class MiniAppPushServiceImpl implements PushService, AdminPushService {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Qualifier(value = "userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private MiniAppPushContainer miniAppPushContainer;
    @Autowired
    private WechatFormidService wechatFormidService;


    /**
     * 陪玩师开始接单
     *
     * @param order
     */
    public void receiveOrder(Order order) {
        push(Collections.singletonList(order.getUserId()), order, WechatTemplateMsgEnum.ORDER_TOUSER_AFFIRM_RECEIVE);
    }

    /**
     * 提醒接单
     *
     * @param order
     */
    public void remindReceive(Order order) {
        push(Collections.singletonList(order.getServiceUserId()), order, WechatTemplateMsgEnum.ORDER_TOSERVICE_REMIND_RECEIVE);
    }

    /**
     * 提醒开始
     *
     * @param order
     */
    public void remindStart(Order order) {
        push(Collections.singletonList(order.getServiceUserId()), order, WechatTemplateMsgEnum.ORDER_TOSERVICE_REMIND_START_SERVICE);
    }

    /**
     * 陪玩师已接单
     *
     * @param order
     */
    public void serviceUserAcceptOrder(Order order) {
        push(Collections.singletonList(order.getServiceUserId()), order, WechatTemplateMsgEnum.POINT_TOSERVICE_ORDER_RECEIVING);
    }

    /**
     * 陪玩师开始服务
     *
     * @param order
     * @return
     */
    public void start(Order order) {
        push(Collections.singletonList(order.getUserId()), order, WechatTemplateMsgEnum.ORDER_TOUSER_START_SERVICE);
    }


    /**
     * 申请协商处理
     *
     * @param order
     */
    public void consult(Order order) {
        push(Collections.singletonList(order.getServiceUserId()), order, WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT);
    }

    /**
     * 拒绝协商处理
     *
     * @param order
     */
    public void rejectConsult(Order order) {
        push(Collections.singletonList(order.getUserId()), order, WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_REJECT);
    }

    /**
     * 同意协商处理
     *
     * @param order
     */
    public void agreeConsult(Order order) {
        push(Collections.singletonList(order.getUserId()), order, WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_AGREE);
    }

    /**
     * 取消协商处理
     *
     * @param order
     */
    public void cancelConsult(Order order) {
        push(Collections.singletonList(order.getServiceUserId()), order, WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL);
    }

    /**
     * 陪玩师取消订单，因为太忙
     *
     * @param order
     */
    public void cancelOrderByServer(Order order) {
        push(Collections.singletonList(order.getUserId()), order, WechatTemplateMsgEnum.ORDER_TOUSER_REJECT_RECEIVE);
    }

    /**
     * 用户取消订单
     *
     * @param order
     */
    public void cancelOrderByUser(Order order) {
        if (order.getServiceUserId() != null) {
            push(Collections.singletonList(order.getServiceUserId()), order, WechatTemplateMsgEnum.ORDER_TOSERVICE_ORDER_CANCEL);
        }
    }

    /**
     * 陪玩师申请仲裁
     *
     * @param order
     */
    public void appealByServer(Order order) {
        push(Collections.singletonList(order.getUserId()), order, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL);
    }

    /**
     * 用户申请仲裁
     *
     * @param order
     */
    public void appealByUser(Order order) {
        push(Collections.singletonList(order.getServiceUserId()), order, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL);
    }

    /**
     * 陪玩师提交验收订单
     *
     * @param order
     */
    public void checkOrder(Order order) {
        push(Collections.singletonList(order.getUserId()), order, WechatTemplateMsgEnum.ORDER_TOUSER_CHECK);
    }

    /**
     * 订单支付
     *
     * @param order
     */
    public void orderPay(Order order) {
        push(Collections.singletonList(order.getServiceUserId()),
                order,
                WechatTemplateMsgEnum.ORDER_TOSERVICE_PAY);
    }

    /**
     * 用户验收订单
     *
     * @param order
     */
    public void acceptOrder(Order order) {
        push(Collections.singletonList(order.getServiceUserId()), order, WechatTemplateMsgEnum.ORDER_TOSERVICE_AFFIRM_SERVER);
    }

    /**
     * 发放优惠券
     *
     * @param userId
     * @param deduction
     */
    public void grantCouponMsg(int userId, String deduction) {
        push(Collections.singletonList(userId), WechatTemplateMsgEnum.GRANT_COUPON, deduction);
    }

    protected abstract void push(List<Integer> userId, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces);

    protected abstract void push(List<Integer> userId, Order order, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces);

    /**
     * 推送IM消息通知
     *
     * @param content
     * @param acceptImId 接收者IMid
     * @param imId       发送者IMid
     * @return
     */
    @UserScore(type = UserScoreEnum.IM_REPLY)
    public String pushIMWxTemplateMsg(String content,
                                      String acceptImId,
                                      String imId) {
        User acceptUser = userService.findByImId(acceptImId);

        UserInfoAuth uia = userInfoAuthService.findByUserId(acceptUser.getId());

        if (uia != null && uia.getOpenSubstituteIm() != null && uia.getOpenSubstituteIm().booleanValue()) {
            return "用户开启了代聊,不推送微信消息!";
        }

        if (acceptUser.getOpenId() == null) {
            log.error("acceptImId为:{}", acceptImId);
            throw new ServiceErrorException("AcceptIM不存在!");
        }
        //判断用户是否在线,在线状态不推送消息
        if (redisOpenService.hasKey(RedisKeyEnum.USER_ONLINE_KEY.generateKey(acceptUser.getId()))) {
            return "用户在线,不推送微信消息!";
        }

        String sendUserName;
        //TODO 千万不要硬编码 @王彬 修改  2018-10-11
        if ("zhouxj".equals(imId)) {
            sendUserName = "小秘书";
        } else {
            User sendUser = userService.findByImId(imId);
            if (sendUser == null) {
                throw new ServiceErrorException("IM不存在!");
            }
            sendUserName = sendUser.getNickname();
        }

        String timeStr = redisOpenService.get(RedisKeyEnum.WX_TEMPLATE_MSG.generateKey(imId + "|" + acceptImId));
        int time = (timeStr == null ? 0 : Integer.valueOf(timeStr));
        if (time >= 10) {
            return "推送次数太多不能推送!";
        }
        push(Collections.singletonList(acceptUser.getId()), WechatTemplateMsgEnum.IM_MSG_PUSH, sendUserName, content);
        time += 1;
        //推送状态缓存两个小时
        redisOpenService.set(RedisKeyEnum.WX_TEMPLATE_MSG.generateKey(imId + "|" + acceptImId), time + "", Constant.TIME_MINUTES_FIFE);
        return "消息推送成功!";
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
    protected synchronized void addTemplateMsg2Queue(int platform,
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
        int size = 1000;
        for (int i = 0; ; i = +size) {
            List<WechatFormidVO> wechatFormidVOS = null;
            try {
                long findStartTime = System.currentTimeMillis();
                wechatFormidVOS = wechatFormidService.findByUserIds(platform, userIds, i, size);
                long findEndTime = System.currentTimeMillis();
                log.info("pushTask:{}执行wechatFormidService.findByUserIds:{}", pushId, findEndTime - findStartTime);
                if (wechatFormidVOS.isEmpty()) {
                    break;
                }
                List<String> formIds = new ArrayList<>();
                //发送微信模板消息
                for (WechatFormidVO wechatFormidVO : wechatFormidVOS) {
                    if (StringUtils.isEmpty(wechatFormidVO.getFormId())) {
                        continue;
                    }
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
                }
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

}
