package com.fulu.game.core.service.impl.coupon;

import com.fulu.game.core.service.impl.CouponServiceImpl;
import com.fulu.game.core.service.impl.push.AdminPushServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminCouponServiceImpl extends CouponServiceImpl{

    @Autowired
    private AdminPushServiceImpl adminPushService;

    @Override
    public void pushMsgAfterGrantCoupon(int userId, String deduction) {
        adminPushService.grantCouponMsg(userId,deduction);
    }
}
