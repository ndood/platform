package com.fulu.game.core.service.impl.push;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.UserScoreEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.aop.UserScore;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public abstract class MiniAppPushServiceImpl extends PushServiceImpl {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;


    /**
     * 陪玩师开始接单
     *
     * @param order
     */
    public void receiveOrder(Order order) {
        push(order.getUserId(), WechatTemplateMsgEnum.ORDER_TOUSER_AFFIRM_RECEIVE);
    }

    /**
     * 提醒接单
     *
     * @param order
     */
    public void remindReceive(Order order) {
        push(order.getServiceUserId(), WechatTemplateMsgEnum.ORDER_TOSERVICE_REMIND_RECEIVE);
    }

    /**
     * 提醒开始
     *
     * @param order
     */
    public void remindStart(Order order) {
        push(order.getServiceUserId(), WechatTemplateMsgEnum.ORDER_TOSERVICE_REMIND_START_SERVICE);
    }


    /**
     * 陪玩师开始服务
     *
     * @param order
     * @return
     */
    public void start(Order order) {
        push(order.getUserId(), WechatTemplateMsgEnum.ORDER_TOUSER_START_SERVICE);
    }


    /**
     * 申请协商处理
     *
     * @param order
     */
    public void consult(Order order) {
        push(order.getServiceUserId(), WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT);
    }

    /**
     * 拒绝协商处理
     *
     * @param order
     */
    public void rejectConsult(Order order) {
        push(order.getUserId(), WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_REJECT);
    }

    /**
     * 同意协商处理
     *
     * @param order
     */
    public void agreeConsult(Order order) {
        push(order.getUserId(), WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_AGREE);
    }

    /**
     * 取消协商处理
     *
     * @param order
     */
    public void cancelConsult(Order order) {
        push(order.getServiceUserId(), WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL);
    }

    /**
     * 陪玩师取消订单，因为太忙
     *
     * @param order
     */
    public void cancelOrderByServer(Order order) {
        push(order.getUserId(), WechatTemplateMsgEnum.ORDER_TOUSER_REJECT_RECEIVE);
    }

    /**
     * 用户取消订单
     *
     * @param order
     */
    public void cancelOrderByUser(Order order) {
        if(order.getServiceUserId()!=null){
            push(order.getServiceUserId(), WechatTemplateMsgEnum.ORDER_TOSERVICE_ORDER_CANCEL);
        }
    }

    /**
     * 陪玩师申请仲裁
     *
     * @param order
     */
    public void appealByServer(Order order) {
        push(order.getUserId(), WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL);
    }

    /**
     * 用户申请仲裁
     *
     * @param order
     */
    public void appealByUser(Order order) {
        push(order.getServiceUserId(), WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL);
    }

    /**
     * 陪玩师提交验收订单
     *
     * @param order
     */
    public void checkOrder(Order order) {
        push(order.getUserId(), WechatTemplateMsgEnum.ORDER_TOUSER_CHECK);
    }


    /**
     * 用户验收订单
     *
     * @param order
     */
    public void acceptOrder(Order order) {
        push(order.getUserId(), WechatTemplateMsgEnum.ORDER_TOSERVICE_AFFIRM_SERVER);
    }

    /**
     * 发放优惠券
     *
     * @param userId
     * @param deduction
     */
    public void grantCouponMsg(int userId, String deduction) {
        push(userId, WechatTemplateMsgEnum.ORDER_TOSERVICE_AFFIRM_SERVER, deduction);
    }


    protected abstract void push(int userId, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces);


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
        if (acceptUser == null || acceptUser.getOpenId() == null) {
            log.error("acceptImId为:{}", acceptImId);
            throw new ServiceErrorException("AcceptIM不存在!");
        }
        //判断用户是否在线,在线状态不推送消息
        if (redisOpenService.hasKey(RedisKeyEnum.USER_ONLINE_KEY.generateKey(acceptUser.getId()))) {
            return "用户在线,不推送微信消息!";
        }
        User sendUser = userService.findByImId(imId);
        if (sendUser == null) {
            throw new ServiceErrorException("IM不存在!");
        }
        String timeStr = redisOpenService.get(RedisKeyEnum.WX_TEMPLATE_MSG.generateKey(imId + "|" + acceptImId));
        int time = (timeStr == null ? 0 : Integer.valueOf(timeStr));
        if (time >= 10) {
            return "推送次数太多不能推送!";
        }
        push(acceptUser.getId(), WechatTemplateMsgEnum.IM_MSG_PUSH, sendUser.getNickname(), content);
        time += 1;
        //推送状态缓存两个小时
        redisOpenService.set(RedisKeyEnum.WX_TEMPLATE_MSG.generateKey(imId + "|" + acceptImId), time + "", Constant.TIME_MINUTES_FIFE);
        return "消息推送成功!";
    }
}
