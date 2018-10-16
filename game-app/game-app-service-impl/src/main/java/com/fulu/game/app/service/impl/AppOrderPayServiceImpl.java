package com.fulu.game.app.service.impl;

import com.fulu.game.core.service.impl.pay.OrderPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AppOrderPayServiceImpl extends OrderPayServiceImpl {

    @Autowired
    private AppOrderServiceImpl appOrderService;


    @Override
    public void payOrder(Integer payment, String orderNo, BigDecimal actualMoney) {
        appOrderService.payOrder(payment, orderNo, actualMoney);
    }


}
