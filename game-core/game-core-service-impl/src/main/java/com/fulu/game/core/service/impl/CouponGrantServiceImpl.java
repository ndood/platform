package com.fulu.game.core.service.impl;


import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.CouponGroupService;
import com.fulu.game.core.service.CouponService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.CouponGrantDao;
import com.fulu.game.core.entity.CouponGrant;
import com.fulu.game.core.service.CouponGrantService;

import java.util.Date;
import java.util.List;


@Service
public class CouponGrantServiceImpl extends AbsCommonService<CouponGrant,Integer> implements CouponGrantService {

    @Autowired
	private CouponGrantDao couponGrantDao;
    @Autowired
    private CouponGrantService couponGrantService;
    @Autowired
    private CouponGroupService couponGroupService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private CouponService couponService;

    @Override
    public ICommonDao<CouponGrant, Integer> getDao() {
        return couponGrantDao;
    }

    @Override
    public void create(String redeemCode, List<String> mobile, String remark) {
        Admin admin =  adminService.getCurrentUser();
        CouponGroup couponGroup =couponGroupService.findByRedeemCode(redeemCode);
        if(couponGroup==null){
            throw new ServiceErrorException("优惠券兑换码错误！");
        }
        CouponGrant couponGrant = new CouponGrant();
        couponGrant.setCouponGroupId(couponGroup.getId());
        couponGrant.setDeduction(couponGroup.getDeduction());
        couponGrant.setRedeemCode(couponGroup.getRedeemCode());
        couponGrant.setRemark(remark);
        couponGrant.setStartUsefulTime(couponGroup.getStartUsefulTime());
        couponGrant.setEndUsefulTime(couponGroup.getEndUsefulTime());
        couponGrant.setIsNewUser(couponGroup.getIsNewUser());
        couponGrant.setCreateTime(new Date());
        couponGrant.setAdminId(admin.getId());
        couponGrant.setAdminName(admin.getName());
        couponGrantService.create(couponGrant);
        //todo 优惠券发放用户
    }


    //优惠券发放用户
    public void grantCoupon2User(CouponGrant couponGrant,List<String> mobile){

    }


}
