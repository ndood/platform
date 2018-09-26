package com.fulu.game.app.service.impl;


import com.fulu.game.core.service.impl.pay.VirtualOrderPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class AppVirtualOrderPayServiceImpl extends VirtualOrderPayServiceImpl {

    @Autowired
    private AppVirtualPayOrderServiceImpl appVirtualPayOrderService;

    @Override
    public void payOrder(String orderNo, BigDecimal actualMoney) {
        appVirtualPayOrderService.successPayOrder(orderNo,actualMoney);
    }



}
