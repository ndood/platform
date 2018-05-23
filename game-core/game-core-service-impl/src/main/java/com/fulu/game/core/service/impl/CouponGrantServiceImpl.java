package com.fulu.game.core.service.impl;


import com.fulu.game.common.exception.CouponException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.CouponGrantDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
public class CouponGrantServiceImpl extends AbsCommonService<CouponGrant,Integer> implements CouponGrantService {

    @Autowired
	private CouponGrantDao couponGrantDao;
    @Autowired
    private CouponGrantService couponGrantService;
    @Autowired
    private CouponGrantUserService couponGrantUserService;
    @Autowired
    private CouponGroupService couponGroupService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private UserService userService;

    @Override
    public ICommonDao<CouponGrant, Integer> getDao() {
        return couponGrantDao;
    }

    @Override
    public void create(String redeemCode, List<String> mobiles, String remark) {
        Admin admin =  adminService.getCurrentUser();
        CouponGroup couponGroup =couponGroupService.findByRedeemCode(redeemCode);
        if(couponGroup==null){
            throw new CouponException(CouponException.ExceptionCode.REDEEMCODE_ERROR);
        }
        Integer couponCount = couponService.countByCouponGroup(couponGroup.getId());
        if(couponCount>=couponGroup.getAmount()){
            throw new CouponException(CouponException.ExceptionCode.BROUGHT_OUT);
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
        //优惠券发放用户
        grantCoupon2User(couponGrant,mobiles);
    }


    @Override
    public PageInfo<CouponGrant> list(Integer pageNum, Integer pageSize, String orderBy) {
        if(StringUtils.isBlank(orderBy)){
            orderBy = "id desc";
        }
        PageHelper.startPage(pageNum,pageSize,orderBy);
        List<CouponGrant> list =findAll();
        return new PageInfo<>(list);
    }


    //优惠券发放用户
    @Transactional
    public void grantCoupon2User(CouponGrant couponGrant,List<String> mobiles){
        String redeemCode = couponGrant.getRedeemCode();
        for(String mobile :mobiles){
           User user = userService.findByMobile(mobile);
           if(user==null){
               couponGrantUserService.create(null,couponGrant.getId(),null,mobile,false,"用户不存在!");
               continue;
           }
           try {
               Coupon coupon =couponService.generateCoupon(redeemCode,user.getId());
               couponGrantUserService.create(coupon.getCouponNo(),couponGrant.getId(),user.getId(),mobile,true,null);
           }catch (CouponException e){
               String errorCause = "没有可用优惠券!";
               switch (e.getExceptionCode()){
                   case BROUGHT_OUT:
                       errorCause="没有可用优惠券!";
                       break;
                   case ALREADY_RECEIVE:
                       errorCause="用户已领用该优惠券!";
                       break;
                   case REDEEMCODE_ERROR:
                       errorCause="优惠券兑换码错误!";
                       break;
                   case NEWUSER_RECEIVE:
                       errorCause="老用户不能发放新人优惠券!";
                       break;
                   case OVERDUE:
                       errorCause="该优惠券已过期!";
                       break;
                   default:
                       break;
               }
               couponGrantUserService.create(null,couponGrant.getId(),user.getId(),mobile,false,errorCause);
           }
        }
    }


}
