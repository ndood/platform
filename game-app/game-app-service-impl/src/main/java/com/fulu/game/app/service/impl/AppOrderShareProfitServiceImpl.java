package com.fulu.game.app.service.impl;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.OrderShareProfitServiceImpl;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class AppOrderShareProfitServiceImpl extends OrderShareProfitServiceImpl {

    @Autowired
    private AppPayServiceImpl appPayService;


    @Override
    protected Boolean refund(Order order, BigDecimal actualMoney, BigDecimal refundUserMoney) throws WxPayException {
        return appPayService.refund(order.getOrderNo(), actualMoney, refundUserMoney);
    }
}