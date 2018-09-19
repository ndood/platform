package com.fulu.game.point.service.impl;

import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.core.service.impl.pay.OrderPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PointMiniAppPayServiceImpl extends OrderPayServiceImpl {


    @Autowired
    private PointMiniAppOrderServiceImpl pointMiniAppOrderService;

    @Override
    public void payOrder(Integer payment, String orderNo, BigDecimal actualMoney) {
        pointMiniAppOrderService.payOrder(orderNo, actualMoney);
    }

}
