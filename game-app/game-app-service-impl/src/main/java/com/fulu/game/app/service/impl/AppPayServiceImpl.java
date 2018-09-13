package com.fulu.game.app.service.impl;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.impl.pay.PayServiceImpl;
import com.github.binarywang.wxpay.exception.WxPayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AppPayServiceImpl extends PayServiceImpl {

    @Autowired
    private AppOrderServiceImpl appOrderService;

    @Override
    protected void payOrder(String orderNo, BigDecimal actualMoney) {
        appOrderService.payOrder(orderNo,actualMoney);
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
