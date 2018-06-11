package com.fulu.game.core.dao;

import com.fulu.game.core.entity.CouponGrant;
import com.fulu.game.core.entity.vo.CouponGrantVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 优惠券发放记录
 * @author wangbin
 * @email ${email}
 * @date 2018-05-15 18:15:28
 */
@Mapper
public interface CouponGrantDao extends ICommonDao<CouponGrant,Integer>{

    List<CouponGrant> findByParameter(CouponGrantVO couponGrantVO);

}
