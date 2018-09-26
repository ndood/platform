package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderMsg;



/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-09-24 22:35:17
 */
public interface OrderMsgService extends ICommonService<OrderMsg,Integer>{


    OrderMsg createServerOrderMsg(Order order, String message);


    OrderMsg createUserOrderMsg(Order order, String message);
	
}
