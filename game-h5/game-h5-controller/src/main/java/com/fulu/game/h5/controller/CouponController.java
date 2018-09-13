package com.fulu.game.h5.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.CouponService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.h5.controller.BaseController;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/13 17:41
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/coupon")
public class CouponController extends BaseController {
    private final CouponService couponService;
    private final UserService userService;

    @Autowired
    public CouponController(CouponService couponService, UserService userService) {
        this.couponService = couponService;
        this.userService = userService;
    }

    /**
     * 获取优惠券列表
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @param isUse    是否已使用（true:已使用； false：未使用）
     * @param overdue  是否已过期（true：已过期；false：未过期）
     * @return 封装结果集
     */
    @PostMapping("/list")
    public Result list(@RequestParam("pageNum") Integer pageNum,
                       @RequestParam("pageSize") Integer pageSize,
                       @RequestParam("isUse") Boolean isUse,
                       @RequestParam("overdue") Boolean overdue) {
        User user = userService.getCurrentUser();
        //判断新老用戶
        Boolean isOldUser = userService.isOldUser(user.getId());
        PageInfo<Coupon> pageInfo = couponService.listByUseStatus(pageNum, pageSize, isUse, overdue);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isOldUser", isOldUser);
        resultMap.put("pageInfo", pageInfo);
        return Result.success().data(resultMap).msg("查询成功！");
    }

    /**
     * 查询用户所有可用的优惠券
     *
     * @return 封装结果集
     */
    @PostMapping(value = "/user")
    public Result userCoupons( @RequestParam( required = false)BigDecimal orderMoney,
                               @RequestParam( required = false)Integer categoryId) {
        User user = userService.getCurrentUser();
        List<Coupon> list = couponService.availableCouponList(user.getId(),orderMoney,categoryId);
        return Result.success().data(list);
    }
}
