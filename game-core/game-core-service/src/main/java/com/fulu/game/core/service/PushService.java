package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.PushMsg;

import java.util.List;

/**
 * 定义所有的推送业务
 */
public interface PushService {


    /**
     * 陪玩师开始接单，跟用户发消息
     *
     * @param order
     */
     void receiveOrder(Order order);

    /**
     * 提醒陪玩师接单，跟陪玩师发消息
     *
     * @param order
     */
     void remindReceive(Order order);

    /**
     * 提醒用户开始，跟用户发消息
     *
     * @param order
     */
     void remindStart(Order order);

    /**
     * 陪玩师已接单，跟用户发消息
     *
     * @param order
     */
     void serviceUserAcceptOrder(Order order);

    /**
     * 陪玩师开始服务，跟用户发消息
     *
     * @param order
     * @return
     */
     void start(Order order);


    /**
     * 用户发起申请协商处理，跟陪玩师发消息
     *
     * @param order
     */
     void consult(Order order);

    /**
     * 陪玩师拒绝协商处理，跟用户发消息
     *
     * @param order
     */
     void rejectConsult(Order order);

    /**
     * 陪玩师同意协商处理，跟用户发消息
     *
     * @param order
     */
     void agreeConsult(Order order);

    /**
     * 用户取消协商处理，跟陪玩师发消息
     *
     * @param order
     */
     void cancelConsult(Order order);

    /**
     * 陪玩师取消订单，因为太忙，跟用户发消息
     *
     * @param order
     */
     void cancelOrderByServer(Order order);

    /**
     * 用户取消订单，跟陪玩师发消息
     *
     * @param order
     */
     void cancelOrderByUser(Order order);

    /**
     * 陪玩师申请仲裁，跟用户发消息
     *
     * @param order
     */
     void appealByServer(Order order);

    /**
     * 用户申请仲裁，跟陪玩师发消息
     *
     * @param order
     */
     void appealByUser(Order order);

    /**
     * 陪玩师提交验收订单，跟用户发消息
     *
     * @param order
     */
     void checkOrder(Order order);

    /**
     * 订单支付通知，跟发消息
     * @param order
     */
     void orderPay(Order order);

    /**
     * 用户验收订单
     *
     * @param order
     */
     void acceptOrder(Order order);

    /**
     * 发放优惠券
     *
     * @param userId
     * @param deduction
     */
     void grantCouponMsg(int userId, String deduction);

    /**
     * 后台手动推送消息
     * @param pushMsg
     * @param userIds
     * @param page
     */
     void adminPush(PushMsg pushMsg, List<Integer> userIds, String page);

}