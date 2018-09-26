package com.fulu.game.core.service;


import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.model.RefundModel;
import com.fulu.game.core.entity.payment.res.PayRequestRes;

import java.math.BigDecimal;

public interface BusinessPayService {


    /**
     * 发起支付请求
     *
     * @param payRequestModel
     * @return
     */
    PayRequestRes payRequest(PayRequestModel payRequestModel);


    /**
     * 支付回调
     *
     * @param payCallbackModel
     * @return
     */
    boolean payResult(PayCallbackModel payCallbackModel);


    /**
     * 退款
     *
     * @param refundModel
     * @return
     */

    boolean refund(RefundModel refundModel);



}
