package com.fulu.game.core.dao;

import com.fulu.game.core.entity.CouponGrantUser;
import com.fulu.game.core.entity.vo.CouponGrantUserVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券发放用户
 * @author wangbin
 * @email ${email}
 * @date 2018-05-15 18:15:28
 */
@Mapper
public interface CouponGrantUserDao extends ICommonDao<CouponGrantUser,Integer>{

    List<CouponGrantUser> findByParameter(CouponGrantUserVO couponGrantUserVO);

}
