package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.vo.CouponVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券表
 * @author wangbin
 * @email ${email}
 * @date 2018-05-15 10:41:12
 */
@Mapper
public interface CouponDao extends ICommonDao<Coupon,Integer>{

    List<Coupon> findByParameter(CouponVO couponVO);

}
