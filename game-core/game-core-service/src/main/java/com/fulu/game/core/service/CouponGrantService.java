package com.fulu.game.core.service;

import com.fulu.game.core.entity.CouponGrant;

import java.util.List;


/**
 * 优惠券发放记录
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-15 18:15:28
 */
public interface CouponGrantService extends ICommonService<CouponGrant,Integer>{


    public void create(String redeemCode,List<String> mobile,String remark);

}
