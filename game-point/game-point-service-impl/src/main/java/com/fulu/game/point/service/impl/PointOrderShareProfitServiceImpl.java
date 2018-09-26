package com.fulu.game.point.service.impl;


import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.payment.model.RefundModel;
import com.fulu.game.core.service.impl.OrderShareProfitServiceImpl;
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
    public Boolean refund(Order order, BigDecimal actualMoney, BigDecimal refundUserMoney) {
        RefundModel model = RefundModel.newBuilder(order.getPayment(), PayBusinessEnum.ORDER)
                .userId(order.getUserId())
                .orderNo(order.getOrderNo())
                .refundMoney(refundUserMoney)
                .totalMoney(actualMoney)
                .platform(order.getPlatform())
                .build();

        return pointMiniAppPayService.refund(model);
    }
}
