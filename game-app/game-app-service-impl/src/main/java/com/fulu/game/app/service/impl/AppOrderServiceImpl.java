package com.fulu.game.app.service.impl;


import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.AbOrderOpenServiceImpl;
import com.fulu.game.core.service.impl.push.MiniAppPushServiceImpl;

import java.math.BigDecimal;

public class AppOrderServiceImpl extends AbOrderOpenServiceImpl{


    @Override
    protected void dealOrderAfterPay(Order order) {

    }

    @Override
    protected void shareProfit(Order order) {

    }

    @Override
    protected void orderRefund(Order order, BigDecimal refundMoney) {

    }

    @Override
    protected MiniAppPushServiceImpl getMinAppPushService() {
        return null;
    }
}
