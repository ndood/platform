package com.fulu.game.core.service.impl.payment;


import com.fulu.game.core.service.impl.payment.to.PayCallbackTO;
import com.fulu.game.core.service.impl.payment.to.PayRequestTO;
import com.fulu.game.core.service.impl.payment.vo.PayCallbackVO;
import com.fulu.game.core.service.impl.payment.vo.PayRequestVO;
import com.fulu.game.core.service.impl.payment.vo.RefundVO;

public interface PaymentComponent {

    /**
     * 发起支付请求
     * @param paymentVO
     * @return
     */
    PayRequestTO payRequest(PayRequestVO paymentVO);


    /**
     * 支付回调
     * @param payCallbackVO
     * @return
     */
    PayCallbackTO payCallBack(PayCallbackVO payCallbackVO);


    /**
     * 退款
     * @param refundVO
     * @return
     */
    boolean refund(RefundVO refundVO);

}
