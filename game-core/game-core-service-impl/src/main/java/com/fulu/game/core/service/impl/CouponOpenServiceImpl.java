package com.fulu.game.core.service.impl;

import com.fulu.game.common.exception.CouponException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.CouponGroupService;
import com.fulu.game.core.service.CouponOpenService;
import com.fulu.game.core.service.CouponService;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public abstract class CouponOpenServiceImpl implements CouponOpenService{

    @Autowired
    private CouponGroupService couponGroupService;
    @Autowired
    private UserService userService;
    @Autowired
    private CouponService couponService;

    /**
     * 发放完优惠券推送消息
     *
     * @param userId
     * @param deduction
     */
    public abstract void pushMsgAfterGrantCoupon(int userId, String deduction);


    /**
     * 用户领取优惠券
     *
     * @param redeemCode
     * @param userId
     * @param receiveTime
     * @param receiveIp
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Coupon generateCoupon(String redeemCode,
                                 Integer userId,
                                 Date receiveTime,
                                 String receiveIp) {
        log.info("领取或发放优惠券:redeemCode:{},userId:{},receiveTime:{},receiveIp:{}", redeemCode, userId, receiveTime, receiveIp);
        CouponGroup couponGroup = couponGroupService.findByRedeemCode(redeemCode);
        if (couponGroup == null) {
            throw new CouponException(CouponException.ExceptionCode.REDEEMCODE_ERROR);
        }
        return generateCoupon(couponGroup, userId, receiveTime, receiveIp);
    }




    /**
     * 给用户发放优惠券
     *
     * @param couponGroup
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Coupon generateCoupon(CouponGroup couponGroup, Integer userId, Date receiveTime, String receiveIp) throws CouponException {
        Integer couponCount = couponService.countByCouponGroup(couponGroup.getId());
        if (couponCount >= couponGroup.getAmount()) {
            throw new CouponException(CouponException.ExceptionCode.BROUGHT_OUT);
        }
        List<Coupon> coupons = couponService.findByUserReceive(couponGroup.getId(), userId);
        if (coupons.size() > 0) {
            throw new CouponException(CouponException.ExceptionCode.ALREADY_RECEIVE);
        }
        //新用户专享卷只能新用户领
        if (userService.isOldUser(userId) && couponGroup.getIsNewUser()) {
            throw new CouponException(CouponException.ExceptionCode.NEWUSER_RECEIVE);
        }
        //过期的优惠券不能兑换
        if (new Date().after(couponGroup.getEndUsefulTime())) {
            throw new CouponException(CouponException.ExceptionCode.OVERDUE);
        }
        String couponNo = generateCouponNo();
        User user = userService.findById(userId);
        Coupon coupon = new Coupon();
        coupon.setCouponGroupId(couponGroup.getId());
        coupon.setCouponNo(couponNo);
        coupon.setReceiveTime(receiveTime);
        coupon.setReceiveIp(receiveIp);
        coupon.setDeduction(couponGroup.getDeduction());
        coupon.setIsNewUser(couponGroup.getIsNewUser());
        coupon.setType(couponGroup.getType());
        coupon.setCategoryId(couponGroup.getCategoryId());
        coupon.setFullReduction(couponGroup.getFullReduction());
        coupon.setUserId(userId);
        coupon.setMobile(user.getMobile());
        coupon.setIsUse(false);
        coupon.setStartUsefulTime(couponGroup.getStartUsefulTime());
        coupon.setEndUsefulTime(couponGroup.getEndUsefulTime());
        coupon.setCreateTime(new Date());
        coupon.setIsFirstReceive(true);
        //判断是否是首次领取
        Integer countUser = couponService.countByUser(userId);
        if (countUser > 0) {
            coupon.setIsFirstReceive(false);
        }
        try {
            couponService.create(coupon);
        } catch (Exception e) {
            log.error("创建优惠券异常",e);
            log.error("无法给用户userId:{}发放优惠券，兑换码为:{}", userId, couponGroup.getRedeemCode());
            return null;
        }
        log.info("生成优惠券:coupon:{}", coupon);

        //发放优惠券通知
        pushMsgAfterGrantCoupon(coupon.getUserId(), coupon.getDeduction().toString());
        return coupon;
    }


    /**
     * 生成优惠券编码
     *
     * @return
     */
    private String generateCouponNo() {
        String couponNo = GenIdUtil.GetCouponNo();
        if (couponService.findByCouponNo(couponNo) == null) {
            return couponNo;
        } else {
            return generateCouponNo();
        }
    }
}
