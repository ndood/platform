package com.fulu.game.core.service.impl.push;

import com.fulu.game.core.entity.Order;

/**
 * 定义所有的推送业务
 */
public interface IBusinessPushService {


    /**
     * 陪玩师开始接单
     *
     * @param order
     */
     void receiveOrder(Order order);

    /**
     * 提醒接单
     *
     * @param order
     */
     void remindReceive(Order order);

    /**
     * 提醒开始
     *
     * @param order
     */
     void remindStart(Order order);

    /**
     * 陪玩师已接单
     *
     * @param order
     */
     void serviceUserAcceptOrder(Order order);

    /**
     * 陪玩师开始服务
     *
     * @param order
     * @return
     */
     void start(Order order);


    /**
     * 申请协商处理
     *
     * @param order
     */
     void consult(Order order);

    /**
     * 拒绝协商处理
     *
     * @param order
     */
     void rejectConsult(Order order);

    /**
     * 同意协商处理
     *
     * @param order
     */
     void agreeConsult(Order order);

    /**
     * 取消协商处理
     *
     * @param order
     */
     void cancelConsult(Order order);

    /**
     * 陪玩师取消订单，因为太忙
     *
     * @param order
     */
     void cancelOrderByServer(Order order);

    /**
     * 用户取消订单
     *
     * @param order
     */
     void cancelOrderByUser(Order order);

    /**
     * 陪玩师申请仲裁
     *
     * @param order
     */
     void appealByServer(Order order);

    /**
     * 用户申请仲裁
     *
     * @param order
     */
     void appealByUser(Order order);

    /**
     * 陪玩师提交验收订单
     *
     * @param order
     */
     void checkOrder(Order order);

    /**
     * 订单支付通知
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

}