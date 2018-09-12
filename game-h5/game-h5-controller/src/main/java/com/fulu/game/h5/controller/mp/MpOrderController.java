package com.fulu.game.h5.controller.mp;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.VirtualPayOrderService;
import com.fulu.game.h5.controller.BaseController;
import com.fulu.game.h5.service.impl.mp.MpPayServiceImpl;
import com.fulu.game.h5.utils.RequestUtil;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 公众号订单Controller
 *
 * @author Gong ZeChun
 * @date 2018/9/4 17:17
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/mp/charge-order")
public class MpOrderController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private MpPayServiceImpl payService;
    @Qualifier("virtualPayOrderServiceImpl")
    @Autowired
    private VirtualPayOrderService virtualPayOrderService;

    /**
     * 提交虚拟币充值订单
     *
     * @param request      request
     * @param sessionkey   订单校验令牌
     * @param virtualMoney 充值虚拟币金额（钻石）
     * @param payment      支付方式（1：微信支付；2：余额支付）
     * @return 封装结果集
     */
    @PostMapping("/submit")
    public Result submit(HttpServletRequest request,
                         @RequestParam String sessionkey,
                         @RequestParam Integer virtualMoney,
                         @RequestParam Integer payment) {
        String ip = RequestUtil.getIpAdrress(request);
        Map<String, Object> resultMap = payService.submit(sessionkey, virtualMoney, ip, payment);
        return Result.success().data(resultMap).msg("创建订单成功!");
    }

    /**
     * 微信支付订单
     *
     * @param request request
     * @param orderNo 订单编号
     * @return 封装结果集
     */
    @RequestMapping(value = "/wechat/pay")
    public Result wechatPay(HttpServletRequest request,
                            @RequestParam String orderNo) {
        String ip = RequestUtil.getIpAdrress(request);
        VirtualPayOrder order = virtualPayOrderService.findByOrderNo(orderNo);
        User user = userService.findById(order.getUserId());
        WxPayMpOrderResult wxPayMpOrderResult = payService.payOrder(order, user, ip);
        Map<String,Object> result = new HashMap<>();
        result.put("appId",wxPayMpOrderResult.getAppId());
        result.put("timestamp",wxPayMpOrderResult.getTimeStamp());
        result.put("nonceStr",wxPayMpOrderResult.getNonceStr());
        result.put("package",wxPayMpOrderResult.getPackageValue());
        result.put("signType",wxPayMpOrderResult.getSignType());
        result.put("paySign",wxPayMpOrderResult.getPaySign());
        return Result.success().data(result);
    }

    /**
     * 提交余额充值订单
     *
     * @param request    request
     * @param sessionkey 订单校验令牌
     * @param money      充值到平台的金额
     * @return 封装结果集
     */
    @PostMapping("/balance/submit")
    public Result balanceCharge(HttpServletRequest request,
                                @RequestParam String sessionkey,
                                @RequestParam BigDecimal money) {
        String ip = RequestUtil.getIpAdrress(request);
        Map<String, Object> resultMap = payService.balanceCharge(sessionkey, money, ip);
        return Result.success().data(resultMap).msg("创建订单成功!");
    }
}
