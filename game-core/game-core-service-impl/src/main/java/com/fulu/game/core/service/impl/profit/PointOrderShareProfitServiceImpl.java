package com.fulu.game.core.service.impl.profit;


import com.fulu.game.core.service.impl.OrderShareProfitServiceImpl;
import com.fulu.game.core.service.impl.pay.PointMiniAppPayServiceImpl;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Slf4j
public class PointOrderShareProfitServiceImpl extends OrderShareProfitServiceImpl {

    @Autowired
    private PointMiniAppPayServiceImpl pointMiniAppPayService;

    @Override
    public Boolean refund(String orderNo, BigDecimal actualMoney, BigDecimal refundUserMoney) throws WxPayException {
        return pointMiniAppPayService.refund(orderNo, actualMoney, refundUserMoney);
    }
}
