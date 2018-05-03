package com.fulu.game.core.service;


import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;

public interface PayService {

    /**
     * 微信统一下单
     * @return
     */
     Object wechatUnifyOrder(String orderNo,String requestIp);


     String payResult(String xmlResult);

     WxPayRefundResult refund(String orderNo) throws WxPayException;
}
