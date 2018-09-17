package com.fulu.game.core.service.impl.payment;

import java.math.BigDecimal;

public interface PaymentService {










    /**
     * 创建一个支付请求
     * @return  返回支付请求
     */
    Object createPayRequest(BigDecimal actualMoney, String orderNo);




}
