package com.fulu.game.h5.controller.thunder;

import com.fulu.game.common.Result;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.ThunderCodeVO;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.ThunderCodeService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.h5.controller.BaseController;
import com.fulu.game.h5.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 迅雷约玩首页-福利Controller
 *
 * @author Gong ZeChun
 * @date 2018/10/10 16:36
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/thunder/welfare")
public class ThunderWelfareController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ThunderCodeService thunderCodeService;

    @Autowired
    private UserService userService;

    /**
     * 未消费用户获取五元优惠券
     *
     * @return
     */
    @RequestMapping("/new-user/coupon")
    public Result newUserCoupon(HttpServletRequest request) {
        String ipStr = RequestUtil.getIpAdrress(request);
        Coupon coupon = thunderCodeService.newUserCoupon(ipStr);
        if (coupon == null) {
            return Result.success().data(coupon).msg("恭喜你，兑换成功");
        }
        return Result.error().msg("用户未满足奖励条件！");
    }

    /**
     * 查询订单满两小时福利
     *
     * @return 封装结果集
     */
    @RequestMapping("/two-hours/get")
    public Result twoHoursWelfare() {
        ThunderCodeVO thunderCodeVO = thunderCodeService.getTwoHoursWelfare();
        if (thunderCodeVO == null) {
            return Result.error().msg("用户未满足奖励条件！");
        }
        return Result.success().data(thunderCodeVO).msg("领取奖励成功！");
    }

    /**
     * 获取下单一单的福利
     *
     * @return
     */
    @RequestMapping("/one-order/get")
    public Result oneOrderWelfare() {
        ThunderCodeVO thunderCodeVO = thunderCodeService.getOneOrderWelfare();
        if (thunderCodeVO == null) {
            return Result.error().msg("用户未满足奖励条件！");
        }
        return Result.success().data(thunderCodeVO).msg("领取奖励成功！");
    }

    /**
     * 获取下单三单的福利
     *
     * @return
     */
    @RequestMapping("/three-order/get")
    public Result threeOrderWelfare() {
        ThunderCodeVO thunderCodeVO = thunderCodeService.getThreeOrderWelfare();
        if (thunderCodeVO == null) {
            return Result.error().msg("用户未满足奖励条件！");
        }
        return Result.success().data(thunderCodeVO).msg("领取奖励成功！");
    }
}
