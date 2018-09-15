package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.CouponService;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/coupon")
public class CouponController extends BaseController {


    @Autowired
    private CouponService couponService;

    @Autowired
    private UserService userService;

    /**
     * 查询用户所有可用的优惠券
     *
     * @return 封装结果集
     */
    @PostMapping(value = "/user")
    public Result userCoupons(@RequestParam( required = false)BigDecimal orderMoney,
                              @RequestParam( required = false)Integer categoryId) {
        User user = userService.getCurrentUser();
        List<Coupon> list = couponService.availableCouponList(user.getId(),orderMoney,categoryId);
        return Result.success().data(list);
    }
}
