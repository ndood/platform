package com.fulu.game.h5.service.impl.fenqile;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.OrderShareProfitServiceImpl;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class H5OrderShareProfitServiceImpl extends OrderShareProfitServiceImpl {


    @Autowired
    private H5FenqilePayServiceImpl h5FenqilePayService;


    @Override
    public Boolean refund(Order order, BigDecimal actualMoney, BigDecimal refundUserMoney) throws WxPayException {

        return h5FenqilePayService.refund(order.getOrderNo(), actualMoney, refundUserMoney);
    }
}
