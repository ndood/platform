package com.fulu.game.core.service;

import com.fulu.game.core.entity.Coupon;
import com.github.pagehelper.PageInfo;


/**
 * 优惠券表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-15 10:41:12
 */
public interface CouponService extends ICommonService<Coupon,Integer>{


    PageInfo<Coupon> listByGroup(Integer couponGroupId, Integer pageNum, Integer pageSize, String orderBy);
	
}
