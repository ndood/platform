package com.fulu.game.core.service;

import com.fulu.game.core.entity.CouponGrantUser;
import com.github.pagehelper.PageInfo;

import java.util.List;


/**
 * 优惠券发放用户
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-15 18:15:28
 */
public interface CouponGrantUserService extends ICommonService<CouponGrantUser,Integer>{


    int create(String couponNo,Integer couponGrantId,Integer userId,String mobile,Boolean isSuccess,String errorCause);

    List<CouponGrantUser> findByGrantId(int grantId);


    PageInfo<CouponGrantUser> list(Integer grantId, Integer pageNum, Integer pageSize, String orderBy);
}
