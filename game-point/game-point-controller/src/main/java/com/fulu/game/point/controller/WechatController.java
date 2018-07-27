package com.fulu.game.point.controller;

import com.fulu.game.common.enums.WechatEcoEnum;
import com.fulu.game.core.service.PayService;
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
    private PayService payService;

    @ResponseBody
    @RequestMapping("/pay/callback")
    public String payNotify(HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            return payService.payResult(xmlResult, WechatEcoEnum.PLAY);
        }catch (Exception e){
            log.error("xml消息转换异常{}", e.getMessage());
            return WxPayNotifyResponse.fail(e.getMessage());
        }

    }

}
