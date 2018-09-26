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
    public void receiveOrder(Order order);

    /**
     * 提醒接单
     *
     * @param order
     */
    public void remindReceive(Order order);

    /**
     * 提醒开始
     *
     * @param order
     */
    public void remindStart(Order order);

    /**
     * 陪玩师已接单
     *
     * @param order
     */
    public void serviceUserAcceptOrder(Order order);

    /**
     * 陪玩师开始服务
     *
     * @param order
     * @return
     */
    public void start(Order order);


    /**
     * 申请协商处理
     *
     * @param order
     */
    public void consult(Order order);

    /**
     * 拒绝协商处理
     *
     * @param order
     */
    public void rejectConsult(Order order);

    /**
     * 同意协商处理
     *
     * @param order
     */
    public void agreeConsult(Order order);

    /**
     * 取消协商处理
     *
     * @param order
     */
    public void cancelConsult(Order order);

    /**
     * 陪玩师取消订单，因为太忙
     *
     * @param order
     */
    public void cancelOrderByServer(Order order);

    /**
     * 用户取消订单
     *
     * @param order
     */
    public void cancelOrderByUser(Order order);

    /**
     * 陪玩师申请仲裁
     *
     * @param order
     */
    public void appealByServer(Order order);

    /**
     * 用户申请仲裁
     *
     * @param order
     */
    public void appealByUser(Order order);

    /**
     * 陪玩师提交验收订单
     *
     * @param order
     */
    public void checkOrder(Order order);

    /**
     * 订单支付通知
     * @param order
     */
    public void orderPay(Order order);

    /**
     * 用户验收订单
     *
     * @param order
     */
    public void acceptOrder(Order order);

    /**
     * 发放优惠券
     *
     * @param userId
     * @param deduction
     */
    public void grantCouponMsg(int userId, String deduction);

}