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
    @RequestMapping("/pay/order/callback")
    public String payOrderNotify(){

        return null;
    }

    @ResponseBody
    @RequestMapping("/pay/virtualproduct/callback")
    public String virtualProductOrderNotify(){

        return null;
    }


}
