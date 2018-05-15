package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.service.CouponGroupService;
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



    @PostMapping(value = "generate")
    public Result generate(@Valid CouponGroup couponGroup){

        System.out.println(couponGroup);

        return Result.success();
    }

}
