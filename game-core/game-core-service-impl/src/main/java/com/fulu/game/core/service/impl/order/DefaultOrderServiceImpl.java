package com.fulu.game.core.service.impl.order;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.OrderServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by burgl on 2018/8/11.
 * 此类无实际意义，是为了解决OrderServiceImpl是抽象类，
 * 在productServiceImpl无法实际使用而建，如有更好的方法，可修改
 */
@Service
public class DefaultOrderServiceImpl extends OrderServiceImpl {
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
