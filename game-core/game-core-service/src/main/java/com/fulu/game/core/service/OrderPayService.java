package com.fulu.game.core.service;


import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.model.RefundModel;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import com.github.binarywang.wxpay.exception.WxPayException;

import java.math.BigDecimal;

public interface OrderPayService {


    /**
     * 发起支付请求
     * @param payRequestModel
     * @return
     */
    PayRequestRes payRequest(PayRequestModel payRequestModel);


    /**
     * 支付回调
     * @param payCallbackModel
     * @return
     */
    boolean payResult(PayCallbackModel payCallbackModel);


    /**
     * 退款
     * @param refundModel
     * @return
     */

    boolean refund(RefundModel refundModel);



    void payOrder(Integer payment,String orderNo, BigDecimal actualMoney);

}
