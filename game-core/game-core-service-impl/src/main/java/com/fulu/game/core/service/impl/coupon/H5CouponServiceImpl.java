package com.fulu.game.core.service.impl.coupon;

import com.fulu.game.core.service.impl.CouponServiceImpl;
import org.springframework.stereotype.Service;

/**
 * H5优惠券实现类
 *
 * @author Gong ZeChun
 * @date 2018/8/13 17:45
 */
@Service
public class H5CouponServiceImpl extends CouponServiceImpl {

    @Override
    public void pushMsgAfterGrantCoupon(int userId, String deduction) {

    }
}
