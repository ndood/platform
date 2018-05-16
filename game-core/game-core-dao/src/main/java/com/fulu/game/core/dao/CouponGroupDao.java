package com.fulu.game.core.dao;

import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.entity.vo.CouponGroupVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券组表
 * @author wangbin
 * @email ${email}
 * @date 2018-05-15 10:41:12
 */
@Mapper
public interface CouponGroupDao extends ICommonDao<CouponGroup,Integer>{

    List<CouponGroup> findByParameter(CouponGroupVO couponGroupVO);

}
