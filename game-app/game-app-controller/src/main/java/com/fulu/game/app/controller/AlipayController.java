package com.fulu.game.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "alipay")
@Slf4j
public class AlipayController {



    /**
     * 支付宝支付回调地址
     * http://t-api-app.wzpeilian.com/alipay/pay/callback
     * @return
     */
    @ResponseBody
    @RequestMapping("/pay/callback")
    public String payNotify(){


        return null;
    }
}
