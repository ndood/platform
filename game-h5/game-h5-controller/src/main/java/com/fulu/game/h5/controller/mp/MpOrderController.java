package com.fulu.game.h5.controller.mp;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.DataException;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.VirtualPayOrderService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.payment.BalancePaymentComponent;
import com.fulu.game.core.service.impl.payment.WeChatPayPaymentComponent;
import com.fulu.game.h5.controller.BaseController;
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
    @Qualifier("virtualPayOrderServiceImpl")
    @Autowired
    private VirtualPayOrderService virtualPayOrderService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private BalancePaymentComponent balancePayment;
    @Autowired
    private WeChatPayPaymentComponent weChatPayPayment;

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

        User user = userService.getCurrentUser();

        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:sessionkey:{};payment:{};;userId:{}", sessionkey, payment, user.getId());
            throw new DataException(DataException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }

        int platform = PlatformEcoEnum.MP.getType();
        String ip = RequestUtil.getIpAdrress(request);
        VirtualPayOrder order = virtualPayOrderService.diamondCharge(user.getId(), virtualMoney, payment, platform, ip);

        Map<String, Object> resultMap = new HashMap<>(4);
        try {
            if (payment.equals(PaymentEnum.WECHAT_PAY.getType())) {
                resultMap.put("orderNo", order.getOrderNo());
                resultMap.put("paySuccess", 0);
            } else if (payment.equals(PaymentEnum.BALANCE_PAY.getType())) {
                boolean flag = balancePayment.balancePayVirtualMoney(user.getId(),
                        order.getActualMoney(), order.getOrderNo());
                if (flag) {
                    virtualPayOrderService.successPayOrder(order.getOrderNo(), order.getActualMoney());
                    resultMap.put("orderNo", order.getOrderNo());
                    resultMap.put("paySuccess", 1);
                } else {
                    return Result.error().msg("余额充钻失败！");
                }
            }
            return Result.success().data(resultMap).msg("创建订单成功!");
        } finally {
            redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));
        }
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
        VirtualPayOrder order = virtualPayOrderService.findByOrderNo(orderNo);
        User user = userService.findById(order.getUserId());

        PayRequestModel model = PayRequestModel.newBuilder().virtualPayOrder(order).user(user).build();
        PayRequestRes payRequestRes = weChatPayPayment.payRequest(model);
        WxPayMpOrderResult wxPayMpOrderResult = (WxPayMpOrderResult) payRequestRes.getRequestParameter();
        Map<String, Object> result = new HashMap<>();
        if (wxPayMpOrderResult != null) {
            result.put("appId", wxPayMpOrderResult.getAppId());
            result.put("timestamp", wxPayMpOrderResult.getTimeStamp());
            result.put("nonceStr", wxPayMpOrderResult.getNonceStr());
            result.put("package", wxPayMpOrderResult.getPackageValue());
            result.put("signType", wxPayMpOrderResult.getSignType());
            result.put("paySign", wxPayMpOrderResult.getPaySign());
        }
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
        User user = userService.getCurrentUser();
        int payment = PaymentEnum.WECHAT_PAY.getType();
        int payPath = PlatformEcoEnum.MP.getType();
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:sessionkey:{};payment:{};;userId:{}", sessionkey, payment, user.getId());
            throw new DataException(DataException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }

        try {
            String ip = RequestUtil.getIpAdrress(request);
            VirtualPayOrder virtualPayOrder = virtualPayOrderService.balanceCharge(user.getId(), money, payment, payPath, ip);
            Map<String, Object> resultMap = new HashMap<>(4);
            resultMap.put("orderNo", virtualPayOrder.getOrderNo());
            return Result.success().data(resultMap).msg("创建订单成功!");
        } finally {
            redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));
        }
    }
}
