package com.fulu.game.core.service.impl.profit;


import com.fulu.game.core.service.impl.OrderShareProfitServiceImpl;
import com.fulu.game.core.service.impl.pay.PlayMiniAppPayServiceImpl;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Slf4j
public class PlayOrderShareProfitServiceImpl extends OrderShareProfitServiceImpl {

    @Autowired
    private PlayMiniAppPayServiceImpl playMiniAppPayService;

    @Override
    public Boolean refund(String orderNo, BigDecimal actualMoney, BigDecimal refundUserMoney) throws WxPayException {
        return playMiniAppPayService.refund(orderNo, actualMoney, refundUserMoney);
    }
}
