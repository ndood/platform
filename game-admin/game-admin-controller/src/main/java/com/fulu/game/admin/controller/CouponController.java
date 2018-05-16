package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.service.CouponGrantService;
import com.fulu.game.core.service.CouponGroupService;
import com.fulu.game.core.service.CouponService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/coupon")
public class CouponController extends BaseController{

    @Autowired
    private CouponGroupService couponGroupService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponGrantService couponGrantService;

    /**
     * 批量生产优惠券
     * @param couponGroup
     * @return
     */
    @PostMapping(value = "generate")
    public Result generate(@Valid CouponGroup couponGroup){
        couponGroupService.create(couponGroup);
        return Result.success().msg("批量生产优惠券成功!");
    }


    /**
     * 优惠券列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "list")
    public Result list(Integer pageNum,
                       Integer pageSize){
        PageInfo<CouponGroup> pageInfo = couponGroupService.list(pageNum,pageSize,null);
        return Result.success().data(pageInfo);
    }


    /**
     * 优惠券详情
     * @param id
     * @return
     */
    @PostMapping(value = "details")
    public Result details(Integer id,
                          Integer pageNum,
                          Integer pageSize){
        PageInfo<Coupon> pageInfo =  couponService.listByGroup(id,pageNum,pageSize,null);
        return Result.success().data(pageInfo);
    }


    /**
     * 优惠券发放
     * @param id
     * @param mobiles
     * @param remark
     * @return
     */
    @PostMapping(value = "grant")
    public Result couponGrant(Integer id,
                              String mobiles,
                              String remark){

        return Result.success();
    }


}
