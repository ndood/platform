package com.fulu.game.core.service.impl.profit;

import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.OrderShareProfitServiceImpl;
import com.github.binarywang.wxpay.exception.WxPayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by burgl on 2018/8/11.
 */
@Service
public class AdminOrderShareProfitServiceImpl  extends OrderShareProfitServiceImpl {

    @Autowired
    private PlayOrderShareProfitServiceImpl playOrderShareProfitService;

    @Autowired
    private PointOrderShareProfitServiceImpl pointOrderShareProfitService;


    @Override
    protected Boolean refund(Order order, BigDecimal actualMoney, BigDecimal refundUserMoney) throws WxPayException {
        if(OrderTypeEnum.PLATFORM.getType().equals(order.getType())){
           return playOrderShareProfitService.refund(order, actualMoney, refundUserMoney);
        }else if(OrderTypeEnum.POINT.getType().equals(order.getType())){
           return pointOrderShareProfitService.refund(order, actualMoney, refundUserMoney);
        }
        return false;
    }
}
