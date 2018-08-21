package com.fulu.game.admin.service.impl;

import com.fulu.game.core.service.impl.CouponOpenServiceImpl;
import com.fulu.game.core.service.impl.CouponServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminCouponOpenServiceImpl extends CouponOpenServiceImpl {

    @Autowired
    private AdminPushServiceImpl adminPushService;

    @Override
    public void pushMsgAfterGrantCoupon(int userId, String deduction) {
        adminPushService.grantCouponMsg(userId,deduction);
    }
}
