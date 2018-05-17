package com.fulu.game.core.service.impl;

import com.fulu.game.common.exception.CouponException;
import com.fulu.game.core.dao.CouponDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.CouponVO;
import com.fulu.game.core.service.CouponGroupService;
import com.fulu.game.core.service.CouponService;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cms.PasswordRecipientId;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CouponServiceImpl extends AbsCommonService<Coupon, Integer> implements CouponService {

    @Autowired
    private CouponDao couponDao;
    @Autowired
    private CouponGroupService couponGroupService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;




    @Override
    public ICommonDao<Coupon, Integer> getDao() {
        return couponDao;
    }


    @Override
    public PageInfo<Coupon> listByGroup(Integer couponGroupId,
                                        Integer pageNum,
                                        Integer pageSize,
                                        String orderBy) {
        CouponVO param = new CouponVO();
        param.setCouponGroupId(couponGroupId);
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "receive_time desc";
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<Coupon> couponList = couponDao.findByParameter(param);
        return new PageInfo<>(couponList);
    }

    @Override
    public PageInfo<Coupon> listByUseStatus(Integer pageNum, Integer pageSize, Boolean isUse, Boolean overdue) {
        User user = userService.getCurrentUser();
        CouponVO couponVO = new CouponVO();
        couponVO.setIsUse(isUse);
        couponVO.setUserId(user.getId());
        String orderBy = null;
        if (isUse) {
            orderBy = "use_time desc";
        } else {
            orderBy = "receive_time desc";
        }
        //已過期的必須是未使用的
        if (overdue) {
            orderBy = "end_useful_time desc";
            couponVO.setOverdue(overdue);
            couponVO.setIsUse(false);
        }

        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<Coupon> couponList = couponDao.findByParameter(couponVO);
        return new PageInfo<>(couponList);
    }

    public List<Coupon> findByUserReceive(Integer couponGroupId, Integer userId) {
        return couponDao.findByUserReceive(couponGroupId, userId);
    }


    public Integer countByCouponGroup(Integer couponGroupId) {
        CouponVO param = new CouponVO();
        param.setCouponGroupId(couponGroupId);
        return couponDao.countByParameter(param);
    }

    /**
     * 通过兑换码发放优惠券给用户
     *
     * @param redeemCode
     * @param userId
     * @return
     * @throws CouponException
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Coupon generateCoupon(String redeemCode, Integer userId) {
        CouponGroup couponGroup = couponGroupService.findByRedeemCode(redeemCode);
        if (couponGroup == null) {
            throw new CouponException(CouponException.ExceptionCode.REDEEMCODE_ERROR);
        }
        return generateCoupon(couponGroup, userId);
    }

    /**
     * 给用户发放优惠券
     *
     * @param couponGroup
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Coupon generateCoupon(CouponGroup couponGroup, Integer userId) throws CouponException {
        Integer couponCount = countByCouponGroup(couponGroup.getId());
        if (couponCount >= couponGroup.getAmount()) {
            throw new CouponException(CouponException.ExceptionCode.BROUGHT_OUT);
        }
        List<Coupon> coupons = findByUserReceive(couponGroup.getId(), userId);
        if (coupons.size() > 0) {
            throw new CouponException(CouponException.ExceptionCode.ALREADY_RECEIVE);
        }
        //新用户专享卷只能新用户领
        if(orderService.isOldUser(userId)&&couponGroup.getIsNewUser()){
            throw new CouponException(CouponException.ExceptionCode.NEWUSER_RECEIVE);
        }

        User user = userService.findById(userId);
        Coupon coupon = new Coupon();
        coupon.setCouponGroupId(couponGroup.getId());
        coupon.setDeduction(couponGroup.getDeduction());
        coupon.setIsNewUser(couponGroup.getIsNewUser());
        coupon.setUserId(userId);
        coupon.setMobile(user.getMobile());
        coupon.setIsUse(false);
        coupon.setStartUsefulTime(couponGroup.getStartUsefulTime());
        coupon.setEndUsefulTime(couponGroup.getEndUsefulTime());
        coupon.setCreateTime(new Date());
        create(coupon);
        return coupon;
    }
}
