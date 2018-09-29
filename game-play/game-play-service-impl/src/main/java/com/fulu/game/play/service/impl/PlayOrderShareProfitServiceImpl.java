package com.fulu.game.play.service.impl;


import com.fulu.game.common.enums.PaymentEnum;
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
    private PlayMiniAppPayServiceImpl playMiniAppPayService;
    @Autowired
    private H5FenqilePayServiceImpl h5FenqilePayService;


    @Override
    public Boolean refund(Order order, BigDecimal actualMoney, BigDecimal refundUserMoney) throws WxPayException {
        log.info("订单退款:order:{}",order);
        if(PaymentEnum.FENQILE_PAY.getType().equals(order.getPayment())){
            log.info("通过订单支付方式判断为分期乐退款:order:{}",order);
            return h5FenqilePayService.refund(order.getOrderNo(), actualMoney, refundUserMoney);
        }else{
            return playMiniAppPayService.refund(order.getOrderNo(), actualMoney, refundUserMoney);
        }
    }
}
