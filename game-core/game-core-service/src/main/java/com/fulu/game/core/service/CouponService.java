package com.fulu.game.core.service;

import com.fulu.game.core.entity.Coupon;
import com.github.pagehelper.PageInfo;

import java.util.Date;
import java.util.List;

/**
 * 优惠券表
 *
 * @author wangbin
 * @email ${email}
 * @date 2018-05-15 10:41:12
 */
public interface CouponService extends ICommonService<Coupon, Integer> {

    PageInfo<Coupon> listByGroup(Integer couponGroupId, Integer pageNum, Integer pageSize, String orderBy);

    PageInfo<Coupon> listByUseStatus(Integer pageNum, Integer pageSize, Boolean isUse, Boolean overdue);

    /**
     * 根据优惠券组ID查询所有优惠券
     *
     * @param couponGroupId
     * @return
     */
    List<Coupon> findByCouponGroup(int couponGroupId);

    /**
     * 查询优惠券领取数量
     *
     * @param couponGroupId
     * @return
     */
    Integer countByCouponGroup(Integer couponGroupId);

    /**
     * 查询优惠券首次领取数量
     *
     * @param couponGroupId
     * @return
     */
    Integer countByCouponGroupAndIsFirst(Integer couponGroupId);

    /**
     * 查询用户有没有领取的优惠券
     *
     * @param couponGroupId
     * @param userId
     * @return
     */
    List<Coupon> findByUserReceive(Integer couponGroupId, Integer userId);

    /**
     * 更新优惠券使用状态
     *
     * @return
     */
    int updateCouponUseStatus(String orderNo, String userIp, Coupon coupon);



    /**
     * 查询用户优惠券数量
     *
     * @param userId
     * @return
     */
    Integer countByUser(Integer userId);

    /**
     * 通过优惠券编码查询优惠券
     *
     * @param couponNo
     * @return
     */
    Coupon findByCouponNo(String couponNo);

    /**
     * 查询用户所有可用的优惠券
     *
     * @return
     */
    List<Coupon> availableCouponList(Integer userId);

    /**
     * 判断优惠券是否可用
     *
     * @param coupon
     * @return
     */
    Boolean couponIsAvailable(Coupon coupon);

}
