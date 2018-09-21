package com.fulu.game.app.controller;


import com.fulu.game.app.service.impl.AppOrderPayServiceImpl;
import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "wechat")
@Slf4j
public class WechatController {


    @Autowired
    private AppOrderPayServiceImpl appOrderPayService;

    @ResponseBody
    @RequestMapping("/pay/order/callback")
    public String payOrderNotify(HttpServletRequest request) {
        try {
            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            log.info("订单支付方法接收微信回调:{}",xmlResult);
            PayCallbackModel payCallbackModel = PayCallbackModel.newBuilder(PaymentEnum.WECHAT_PAY.getType(), PayBusinessEnum.ORDER)
                    .platform(PlatformEcoEnum.APP.getType())
                    .wechatXmlResult(xmlResult)
                    .build();
            boolean res = appOrderPayService.payResult(payCallbackModel);
            if (res) {
                return WxPayNotifyResponse.success("支付成功");
            }
        } catch (Exception e) {
            log.error("xml消息转换异常", e);
        }
        return WxPayNotifyResponse.fail("支付失败");
    }


    @ResponseBody
    @RequestMapping("/pay/virtualproduct/callback")
    public String virtualProductOrderNotify(HttpServletRequest request) {
        try {
            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            log.info("充值方法接收微信回调:{}",xmlResult);
            PayCallbackModel payCallbackModel = PayCallbackModel.newBuilder(PaymentEnum.WECHAT_PAY.getType(), PayBusinessEnum.VIRTUAL_PRODUCT)
                    .platform(PlatformEcoEnum.APP.getType())
                    .wechatXmlResult(xmlResult)
                    .build();
            boolean res = appOrderPayService.payResult(payCallbackModel);
            if (res) {
                return WxPayNotifyResponse.success("支付成功");
            }
        } catch (Exception e) {
            log.error("xml消息转换异常", e);
        }
        return WxPayNotifyResponse.fail("支付失败");
    }

}
