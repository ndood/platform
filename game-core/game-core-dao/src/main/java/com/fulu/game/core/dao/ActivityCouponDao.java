package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ActivityCoupon;
import com.fulu.game.core.entity.vo.ActivityCouponVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 活动优惠券表
 * @author wangbin
 * @email ${email}
 * @date 2018-09-28 14:32:06
 */
@Mapper
public interface ActivityCouponDao extends ICommonDao<ActivityCoupon,Integer>{

    List<ActivityCoupon> findByParameter(ActivityCouponVO activityCouponVO);

}
