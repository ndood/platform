package com.fulu.game.core.service.impl.payment;

import com.fulu.game.core.entity.vo.PaymentVO;

public interface PaymentService {


    /**
     * 成功支付方法
     *
     * @return
     */
    Boolean paySuccess(PaymentVO paymentVO);


    /**
     * 创建一个支付请求
     *
     * @return 返回支付请求
     */
    Object createPayRequest(PaymentVO paymentVO);


}
