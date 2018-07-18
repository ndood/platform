package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderShareProfit;

import java.math.BigDecimal;


/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-18 13:32:16
 */
public interface OrderShareProfitService extends ICommonService<OrderShareProfit,Integer>{


    public void shareProfit(Order order);

    public void orderRefund(Order order,BigDecimal refundMoney);

}
