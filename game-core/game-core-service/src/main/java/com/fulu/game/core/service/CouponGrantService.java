package com.fulu.game.core.service;

import com.fulu.game.core.entity.CouponGrant;
import com.github.pagehelper.PageInfo;


/**
 * 优惠券发放记录
 *
 * @author wangbin
 * @email ${email}
 * @date 2018-05-15 18:15:28
 */
public interface CouponGrantService extends ICommonService<CouponGrant, Integer> {


    void create(String redeemCode, String userIds, String remark);

    PageInfo<CouponGrant> list(Integer pageNum, Integer pageSize, String orderBy);
}
