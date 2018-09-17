package com.fulu.game.core.service.impl.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService{


    @Override
    public Object createPayRequest(BigDecimal actualMoney, String orderNo) {
        return null;
    }
}
