package com.fulu.game.core.service;

import com.fulu.game.core.entity.CouponGroup;
import com.github.pagehelper.PageInfo;


/**
 * 优惠券组表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-15 10:41:12
 */
public interface CouponGroupService{


    int create(CouponGroup couponGroup);

    CouponGroup findById(Integer id);

    PageInfo<CouponGroup> list(Integer pageNum, Integer pageSize, String orderBy);


}
