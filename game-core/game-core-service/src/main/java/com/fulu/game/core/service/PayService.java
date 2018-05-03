package com.fulu.game.core.service;


import com.github.binarywang.wxpay.exception.WxPayException;

import java.math.BigDecimal;

public interface PayService {

    /**
     * 微信统一下单
     * @return
     */
     Object wechatUnifyOrder(String orderNo,String requestIp);


     String payResult(String xmlResult);

     Boolean refund(String orderNo, BigDecimal totalMoney, BigDecimal RefundMoney) throws WxPayException;


     Boolean refund(String orderNo,BigDecimal totalMoney)throws WxPayException;
}
