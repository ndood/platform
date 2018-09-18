package com.fulu.game.core.service.impl.payment;

import com.fulu.game.core.entity.vo.PaymentVO;

public interface PaymentComponentComponent {


    /**
     * 支付请求
     * @param paymentVO
     * @return
     */
     Object payRequest(PaymentVO paymentVO);


}
