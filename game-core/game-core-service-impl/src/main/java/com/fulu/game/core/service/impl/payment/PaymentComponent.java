package com.fulu.game.core.service.impl.payment;


import com.fulu.game.core.entity.payment.res.PayCallbackRes;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.model.RefundModel;

public interface PaymentComponent {

    /**
     * 发起支付请求
     * @param paymentVO
     * @return
     */
    PayRequestRes payRequest(PayRequestModel paymentVO);


    /**
     * 支付回调
     * @param payCallbackVO
     * @return
     */
    PayCallbackRes payCallBack(PayCallbackModel payCallbackVO);


    /**
     * 退款
     * @param refundVO
     * @return
     */
    boolean refund(RefundModel refundVO);

}
