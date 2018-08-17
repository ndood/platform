package com.fulu.game.core.service.impl.profit;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.OrderShareProfitServiceImpl;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Slf4j
public class H5OrderShareProfitServiceImpl extends OrderShareProfitServiceImpl {

    @Override
    protected <T> T refund(Order order, BigDecimal actualMoney, BigDecimal refundUserMoney) throws WxPayException {
        return null;
    }
}
