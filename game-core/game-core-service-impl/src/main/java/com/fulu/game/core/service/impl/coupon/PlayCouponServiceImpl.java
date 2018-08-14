package com.fulu.game.core.service.impl.coupon;

import com.fulu.game.core.service.impl.CouponServiceImpl;
import com.fulu.game.core.service.impl.push.PlayMiniAppPushServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayCouponServiceImpl extends CouponServiceImpl {

    @Autowired
    private PlayMiniAppPushServiceImpl playMiniAppPushService;


    public void pushMsgAfterGrantCoupon(int userId, String deduction){
        playMiniAppPushService.grantCouponMsg(userId,deduction);
    }

}
