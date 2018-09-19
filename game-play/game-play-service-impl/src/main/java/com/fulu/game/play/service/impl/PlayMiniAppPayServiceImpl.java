package com.fulu.game.play.service.impl;

import com.fulu.game.core.service.impl.pay.OrderPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PlayMiniAppPayServiceImpl extends OrderPayServiceImpl {


    @Qualifier(value = "playMiniAppOrderServiceImpl")
    @Autowired
    private PlayMiniAppOrderServiceImpl playMiniAppOrderService;

    @Override
    public void payOrder(Integer payment, String orderNo, BigDecimal actualMoney) {
        playMiniAppOrderService.payOrder(orderNo, actualMoney);
    }


}
