package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderConsult;

import java.math.BigDecimal;


/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-18 15:40:34
 */
public interface OrderConsultService extends ICommonService<OrderConsult,Integer>{


    OrderConsult createConsult(Order order,int orderStatus,BigDecimal refundMoney);


}
