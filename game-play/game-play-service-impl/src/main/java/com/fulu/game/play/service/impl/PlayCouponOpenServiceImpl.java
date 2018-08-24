package com.fulu.game.play.service.impl;

import com.fulu.game.core.service.impl.CouponOpenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayCouponOpenServiceImpl extends CouponOpenServiceImpl {

    @Autowired
    private PlayMiniAppPushServiceImpl playMiniAppPushService;


    public void pushMsgAfterGrantCoupon(int userId, String deduction) {
        playMiniAppPushService.grantCouponMsg(userId, deduction);
    }

}
