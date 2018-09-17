package com.fulu.game.play.service.impl;


import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.OrderShareProfitServiceImpl;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Slf4j
public class PlayOrderShareProfitServiceImpl extends OrderShareProfitServiceImpl {

    @Autowired
    private MiniAppOrderPayServiceImpl playMiniAppPayService;

    @Override
    public Boolean refund(Order order, BigDecimal actualMoney, BigDecimal refundUserMoney) throws WxPayException {
        return playMiniAppPayService.refund(order.getOrderNo(), actualMoney, refundUserMoney);
    }
}
