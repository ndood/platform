package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.PushMsg;

import java.util.List;

/**
 * 定义所有管理后台的推送业务
 */
public interface AdminPushService {

    /**
     * 后台手动推送消息
     * @param pushMsg
     * @param userIds
     * @param page
     */
     void adminPush(PushMsg pushMsg, List<Integer> userIds, String page);

    /**
     * 客服仲裁，用户胜
     * @param order
     */
     void appealUserWin(Order order);

    /**
     * 客服仲裁，陪玩师胜
     *
     * @param order
     */
    void appealServiceWin(Order order);

    /**
     * 客服仲裁协商处理
     *
     * @param order
     * @param msg
     */
    void appealNegotiate(Order order, String msg);

    /**
     * 发放优惠券
     *
     * @param userId
     * @param deduction
     */
    void grantCouponMsg(int userId, String deduction);

    /**
     * 陪玩师技能审核通过
     */
    void techAuthAuditSuccess(Integer userId);

    /**
     * 陪玩师技能审核通过
     */
    void techAuthAuditFail(Integer userId, String msg);

    /**
     * 陪玩师个人信息审核不通过
     */
    void userInfoAuthFail(Integer userId, String msg);

    /**
     * 陪玩师个人信息审核通过
     *
     * @param userId
     */
    void userInfoAuthSuccess(Integer userId);

}