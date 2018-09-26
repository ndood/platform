package com.fulu.game.h5.service.impl;

import com.fulu.game.core.service.impl.VirtualPayOrderServiceImpl;
import com.fulu.game.core.service.impl.pay.VirtualOrderPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class H5VirtualOrderPayServiceImpl extends VirtualOrderPayServiceImpl {

    @Autowired
    private VirtualPayOrderServiceImpl virtualPayOrderService;


    @Override
    public void payOrder(String orderNo, BigDecimal actualMoney) {
        virtualPayOrderService.successPayOrder(orderNo,actualMoney);
    }

}
