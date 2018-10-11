package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.CouponTypeEnum;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.CouponOpenService;
import com.fulu.game.core.service.CouponService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.play.utils.RequestUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券操作接口
 *
 * @author yanbiao
 * @date 2018.5.16
 */

@RestController
@Slf4j
@RequestMapping("/api/v1/coupon")
public class CouponController extends BaseController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponOpenService couponOpenService;

    @Autowired
    private UserService userService;

    /**
     * 兑换优惠券
     *
     * @return
     */
    @PostMapping("/exchange")
    public Result exchange(@RequestParam("redeemCode") String redeemCode, HttpServletRequest request) {
        User user = userService.getCurrentUser();
        String ipStr = RequestUtil.getIpAdrress(request);
        Coupon coupon = couponOpenService.generateCoupon(redeemCode, user.getId(), new Date(), ipStr);
        return Result.success().data(coupon).msg("恭喜你，兑换成功");
    }


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
    public Result userCoupons(@RequestParam( required = false)BigDecimal orderMoney,
                              @RequestParam( required = false)Integer categoryId) {
        User user = userService.getCurrentUser();
        List<Coupon> list = couponService.availableCouponList(user.getId(),orderMoney,categoryId);
        for(Coupon coupon : list){
            if(CouponTypeEnum.DISCOUNT.getType().equals(coupon.getType())){
                coupon.setDeduction(coupon.getDeduction().multiply(new BigDecimal(10)));
            }
        }
        return Result.success().data(list);
    }
    

}
