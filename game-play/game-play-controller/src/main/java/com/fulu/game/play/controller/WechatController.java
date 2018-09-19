package com.fulu.game.play.controller;

import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.fulu.game.play.service.impl.PlayMiniAppPayServiceImpl;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "wechat")
@Slf4j
public class WechatController {

    @Autowired
    private PlayMiniAppPayServiceImpl playMiniAppPayService;

    @ResponseBody
    @RequestMapping("/pay/callback")
    public String payNotify(HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            PayCallbackModel payCallbackModel = PayCallbackModel.newBuilder(PaymentEnum.WECHAT_PAY.getType(), PayBusinessEnum.ORDER)
                    .platform(PlatformEcoEnum.PLAY.getType())
                    .wechatXmlResult(xmlResult)
                    .build();
            boolean res = playMiniAppPayService.payResult(payCallbackModel);
            if (res) {
                return WxPayNotifyResponse.success("支付成功");
            }
        } catch (Exception e) {
            log.error("xml消息转换异常{}", e.getMessage());
        }
        return WxPayNotifyResponse.fail("支付失败");
    }

}

