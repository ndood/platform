package com.fulu.game.h5.service.impl;

import com.fulu.game.core.service.impl.pay.OrderPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class H5PayServiceImpl extends OrderPayServiceImpl {

    @Autowired
    private H5OrderServiceImpl h5OrderService;

    @Override
    public void payOrder(Integer payment, String orderNo, BigDecimal actualMoney) {
        h5OrderService.payOrder(orderNo, actualMoney);
    }


}
