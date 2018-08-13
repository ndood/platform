package com.fulu.game.core.service.impl.coupon;

import com.fulu.game.core.service.impl.CouponServiceImpl;
import com.fulu.game.core.service.impl.push.PointMiniAppPushServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointCouponServiceImpl extends CouponServiceImpl {

    @Autowired
    private PointMiniAppPushServiceImpl pointMiniAppPushService;


    public void pushMsgAfterGrantCoupon(int userId, String deduction){
        pointMiniAppPushService.grantCouponMsg(userId,deduction);
    }

}
