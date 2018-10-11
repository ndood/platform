package com.fulu.game.admin.service.impl;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.push.PushFactory;
import com.fulu.game.core.service.impl.push.PushServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminPushServiceImpl {

    @Autowired
    private PushFactory pushFactory;

    /**
     * 客服仲裁，用户胜
     *
     * @param order
     */
    public void appealUserWin(Order order) {
        pushFactory.getPushIns().forEach(adminPushService ->
                adminPushService.appealUserWin(order)
        );
    }

    /**
     * 客服仲裁，陪玩师胜
     *
     * @param order
     */
    public void appealServiceWin(Order order) {
        pushFactory.getPushIns().forEach(adminPushService ->
                adminPushService.appealServiceWin(order)
        );
    }

    /**
     * 客服仲裁协商处理
     *
     * @param order
     * @param msg
     */
    public void appealNegotiate(Order order, String msg) {
        pushFactory.getPushIns().forEach(adminPushService ->
                adminPushService.appealNegotiate(order, msg)
        );
    }

    /**
     * 发放优惠券
     *
     * @param userId
     * @param deduction
     */
    public void grantCouponMsg(int userId, String deduction) {
        pushFactory.getPushIns().forEach(adminPushService ->
                adminPushService.grantCouponMsg(userId, deduction)
        );
    }

    /**
     * 陪玩师技能审核通过
     */
    public void techAuthAuditSuccess(Integer userId) {
        pushFactory.getPushIns().forEach(adminPushService ->
                adminPushService.techAuthAuditSuccess(userId)
        );
    }

    /**
     * 陪玩师技能审核通过
     */
    public void techAuthAuditFail(Integer userId, String msg) {
        pushFactory.getPushIns().forEach(adminPushService ->
                adminPushService.techAuthAuditFail(userId, msg)
        );
    }

    /**
     * 陪玩师个人信息审核不通过
     */
    public void userInfoAuthFail(Integer userId, String msg) {
        pushFactory.getPushIns().forEach(adminPushService ->
                adminPushService.userInfoAuthFail(userId, msg)
        );
    }

    /**
     * 陪玩师个人信息审核通过
     *
     * @param userId
     */
    public void userInfoAuthSuccess(Integer userId) {
        pushFactory.getPushIns().forEach(adminPushService ->
                adminPushService.techAuthAuditSuccess(userId)
        );
    }


}
