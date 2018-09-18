package com.fulu.game.app.service.impl;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.impl.pay.OrderPayServiceImpl;
import com.fulu.game.core.service.impl.payment.AlipayPaymentComponent;
import com.fulu.game.core.service.impl.payment.BalancePaymentComponent;
import com.fulu.game.core.service.impl.payment.WeChatPayPaymentComponent;
import com.github.binarywang.wxpay.exception.WxPayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AppOrderPayServiceImpl extends OrderPayServiceImpl {

    @Autowired
    private AppOrderServiceImpl appOrderService;
    @Autowired
    private BalancePaymentComponent balancePayment;
    @Autowired
    private AlipayPaymentComponent alipayPayment;
    @Autowired
    private WeChatPayPaymentComponent weChatPayPayment;

    @Override
    protected void payOrder(Integer payment, String orderNo, BigDecimal actualMoney) {
        appOrderService.payOrder(orderNo, actualMoney);
    }

    @Override
    protected Object pay(Order order, User user, String ip) {
        return null;
    }


    @Override
    protected Object parseResult(String xmlResult) throws WxPayException {
        return null;
    }

    @Override
    protected String getOrderNo(Object result) {
        return null;
    }


    @Override
    protected String getTotal(Object result) {
        return null;
    }


    @Override
    protected boolean thirdRefund(String orderNo, Integer totalMoney, Integer refundMoney) throws WxPayException {
        return false;
    }


}
