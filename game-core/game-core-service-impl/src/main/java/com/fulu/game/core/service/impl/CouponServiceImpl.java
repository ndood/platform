package com.fulu.game.core.service.impl;

import com.fulu.game.core.dao.CouponDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.CouponVO;
import com.fulu.game.core.service.CouponService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CouponServiceImpl extends AbsCommonService<Coupon, Integer> implements CouponService {

    @Autowired
    private CouponDao couponDao;
    @Autowired
    private UserService userService;


    @Override
    public ICommonDao<Coupon, Integer> getDao() {
        return couponDao;
    }

    @Override
    public PageInfo<Coupon> listByGroup(Integer couponGroupId,
                                        Integer pageNum,
                                        Integer pageSize,
                                        String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "receive_time desc";
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<Coupon> couponList = findByCouponGroup(couponGroupId);
        return new PageInfo<>(couponList);
    }


    @Override
    public List<Coupon> availableCouponList(Integer userId) {
        List<Coupon> couponList = couponDao.findByAvailable(userId);
        List<Coupon> availableCouponList = new ArrayList<>();
        availableCouponList.addAll(couponList);
        for (Coupon coupon : couponList) {
            if (userService.isOldUser(userId) && coupon.getIsNewUser()) {
                availableCouponList.remove(coupon);
            }
        }
        return availableCouponList;
    }

    @Override
    public Boolean couponIsAvailable(Coupon coupon) {
        if (coupon.getIsUse()) {
            log.error("优惠券使用错误:已经使用:{}", coupon.getCouponNo());
            return false;
        }
        if (new Date().before(coupon.getStartUsefulTime())) {
            log.error("优惠券使用错误:使用时间未到:{}", coupon.getCouponNo());
            return false;
        }
        if (new Date().after(coupon.getEndUsefulTime())) {
            log.error("优惠券使用错误:过期:{}", coupon.getCouponNo());
            return false;
        }
        if (userService.isOldUser(coupon.getUserId()) && coupon.getIsNewUser()) {
            log.error("优惠券使用错误:不能使用新用户专享券:{}", coupon.getCouponNo());
            return false;
        }
        return true;
    }

    @Override
    public PageInfo<Coupon> listByUseStatus(Integer pageNum, Integer pageSize, Boolean isUse, Boolean overdue) {
        User user = userService.getCurrentUser();
        CouponVO couponVO = new CouponVO();
        couponVO.setIsUse(isUse);
        couponVO.setUserId(user.getId());
        String orderBy;
        if (isUse) {
            orderBy = "use_time desc";
        } else {
            couponVO.setOverdue(overdue);
            orderBy = "receive_time desc";
        }
        //已過期的必須是未使用的
        if (overdue) {
            orderBy = "end_useful_time desc";
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<Coupon> couponList = couponDao.findByParameter(couponVO);
        return new PageInfo<>(couponList);
    }

    @Override
    public List<Coupon> findByUserReceive(Integer couponGroupId, Integer userId) {
        return couponDao.findByUserReceive(couponGroupId, userId);
    }


    @Override
    public int updateCouponUseStatus(String orderNo, String userIp, Coupon coupon) {
        coupon.setOrderNo(orderNo);
        coupon.setIsUse(true);
        coupon.setUseTime(new Date());
        coupon.setUseIp(userIp);
        return update(coupon);
    }

    @Override
    public Integer countByUser(Integer userId) {
        CouponVO param = new CouponVO();
        param.setUserId(userId);
        return couponDao.countByParameter(param);
    }

    /**
     * 查看优惠券领取数量
     *
     * @param couponGroupId
     * @return
     */
    @Override
    public Integer countByCouponGroup(Integer couponGroupId) {
        CouponVO param = new CouponVO();
        param.setCouponGroupId(couponGroupId);
        return couponDao.countByParameter(param);
    }

    /**
     * 查看优惠券首次领取数量
     *
     * @param couponGroupId
     * @return
     */
    @Override
    public Integer countByCouponGroupAndIsFirst(Integer couponGroupId) {
        CouponVO param = new CouponVO();
        param.setCouponGroupId(couponGroupId);
        param.setIsFirstReceive(true);
        return couponDao.countByParameter(param);
    }


    @Override
    public List<Coupon> findByCouponGroup(int couponGroupId) {
        CouponVO param = new CouponVO();
        param.setCouponGroupId(couponGroupId);
        List<Coupon> list = couponDao.findByParameter(param);
        return list;
    }


    @Override
    public Coupon findByCouponNo(String couponNo) {
        CouponVO param = new CouponVO();
        param.setCouponNo(couponNo);
        List<Coupon> list = couponDao.findByParameter(param);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }


}
