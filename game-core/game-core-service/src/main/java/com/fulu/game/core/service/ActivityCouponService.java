package com.fulu.game.core.service;

import com.fulu.game.core.entity.ActivityCoupon;

import java.util.List;


/**
 * 活动优惠券表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-09-28 14:32:06
 */
public interface ActivityCouponService extends ICommonService<ActivityCoupon,Integer>{


    List<ActivityCoupon> findByActivityId(Integer activityId);
}
