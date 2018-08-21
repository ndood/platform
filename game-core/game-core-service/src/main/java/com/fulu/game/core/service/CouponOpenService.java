package com.fulu.game.core.service;

import com.fulu.game.core.entity.Coupon;

import java.util.Date;

public interface CouponOpenService {


    Coupon generateCoupon(String redeemCode, Integer userId, Date receiveTime, String receiveIp);

}
