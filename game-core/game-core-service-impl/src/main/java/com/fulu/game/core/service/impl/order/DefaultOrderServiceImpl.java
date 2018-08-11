package com.fulu.game.core.service.impl.order;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.OrderServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by burgl on 2018/8/11.
 */
@Service
public class DefaultOrderServiceImpl extends OrderServiceImpl{
    @Override
    protected void dealOrderAfterPay(Order order) {

    }

    @Override
    protected void shareProfit(Order order) {

    }

    @Override
    protected void orderRefund(Order order, BigDecimal refundMoney) {

    }
}
