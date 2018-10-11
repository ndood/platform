package com.fulu.game.app.controller;


import com.fulu.game.app.service.impl.AppVirtualOrderPayServiceImpl;
import com.fulu.game.app.service.impl.AppVirtualPayOrderServiceImpl;
import com.fulu.game.app.util.RequestUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.domain.ClientInfo;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.DataException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@Slf4j
@RequestMapping("/api/v1/charge")
public class ChargeController extends BaseController {


    @Autowired
    private AppVirtualPayOrderServiceImpl appVirtualPayOrderService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserService userService;
    @Autowired
    private AppVirtualOrderPayServiceImpl appVirtualOrderPayService;


    /**
     * 余额充值接口
     *
     * @param request
     * @param sessionkey
     * @param money
     * @return
     */
    @RequestMapping(value = "balance")
    public Result balanceCharge(HttpServletRequest request,
                                @RequestParam(required = true) Integer payment,
                                @RequestParam(required = true) String sessionkey,
                                @RequestParam(required = true) BigDecimal money) {
        User user = userService.getCurrentUser();
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:sessionkey:{};payment:{};;userId:{}", sessionkey, payment, user.getId());
            throw new DataException(DataException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }
        try {
            ClientInfo clientInfo = SubjectUtil.getUserClientInfo();
            int platform;
            if (clientInfo != null) {
                if ("ios".equalsIgnoreCase(clientInfo.get_platform())) {
                    platform = PlatformEcoEnum.IOS.getType();
                } else {
                    platform = PlatformEcoEnum.ANDROID.getType();
                }
            } else {
                platform = PlatformEcoEnum.ANDROID.getType();
            }
            String ip = RequestUtil.getIpAdrress(request);
            VirtualPayOrder order = appVirtualPayOrderService.balanceCharge(user.getId(), money, payment, platform, ip);
            PayRequestModel model = PayRequestModel.newBuilder().virtualPayOrder(order).user(user).build();
            PayRequestRes res = appVirtualOrderPayService.payRequest(model);
            return Result.success().data(res);
        } finally {
            redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));
        }
    }


    /**
     * 钻石充值接口
     *
     * @param request
     * @param payment
     * @param sessionkey
     * @return
     */
    @RequestMapping(value = "diamond")
    public Result diamondCharge(HttpServletRequest request,
                                @RequestParam(required = true) String sessionkey,
                                @RequestParam(required = true) Integer virtualMoney,
                                @RequestParam(required = true) Integer payment) {
        User user = userService.getCurrentUser();
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:sessionkey:{};payment:{};;userId:{}", sessionkey, payment, user.getId());
            throw new DataException(DataException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }
        try {
            ClientInfo clientInfo = SubjectUtil.getUserClientInfo();
            int platform;
            if (clientInfo != null) {
                if ("ios".equalsIgnoreCase(clientInfo.get_platform())) {
                    platform = PlatformEcoEnum.IOS.getType();
                } else {
                    platform = PlatformEcoEnum.ANDROID.getType();
                }
            } else {
                platform = PlatformEcoEnum.ANDROID.getType();
            }
            String ip = RequestUtil.getIpAdrress(request);
            VirtualPayOrder order = appVirtualPayOrderService.diamondCharge(user.getId(), virtualMoney, payment, platform, ip);
            PayRequestModel model = PayRequestModel.newBuilder().virtualPayOrder(order).user(user).build();
            PayRequestRes res = appVirtualOrderPayService.payRequest(model);
            return Result.success().data(res);
        } finally {
            redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));
        }
    }


    @RequestMapping(value = "diamond/ios")
    public Result iosDiamond(@RequestParam(required = true) String receiptData,
                             HttpServletRequest request) {
        String ip = RequestUtil.getIpAdrress(request);
        User user = userService.getCurrentUser();
        boolean flag = appVirtualPayOrderService.iosChargeDiamond(receiptData, user.getId(), ip);
        if (flag) {
            return Result.success().msg("支付成功!");
        }
        return Result.error().msg("支付失败!");

    }


}