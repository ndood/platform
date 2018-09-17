package com.fulu.game.app.service.impl;

import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.PayRequestVO;
import com.fulu.game.core.entity.vo.PaymentVO;
import com.fulu.game.core.service.impl.pay.OrderPayServiceImpl;
import com.fulu.game.core.service.impl.payment.PaymentService;
import com.github.binarywang.wxpay.exception.WxPayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AppPayServiceImpl extends OrderPayServiceImpl {

    @Autowired
    private AppOrderServiceImpl appOrderService;
    @Autowired
    private PaymentService paymentService;

    @Override
    protected void payOrder(String orderNo, BigDecimal actualMoney) {
        appOrderService.payOrder(orderNo, actualMoney);
    }


    @Override
    protected PayRequestVO pay(Order order, User user, String ip) {
        PayRequestVO payRequestVO = new PayRequestVO();
        payRequestVO.setPayment(order.getPayment());
        PaymentVO paymentVO = PaymentVO.builder().paymentEnum(PaymentEnum.getEnumByType(order.getPayment()))
                .payBusinessEnum(PayBusinessEnum.ORDER)
                .order(order)
                .user(user)
                .userIp(ip)
                .build();
        //余额支付需要不需要调用支付请求
        if (PaymentEnum.BALANCE_PAY.equals(PaymentEnum.getEnumByType(order.getPayment()))) {
            if(paymentService.paySuccess(paymentVO)){
                payOrder(order.getOrderNo(),order.getActualMoney());
            }
        }else{
            Object payArguments = paymentService.createPayRequest(paymentVO);
            payRequestVO.setPayArguments(payArguments);
        }
        return payRequestVO;
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
