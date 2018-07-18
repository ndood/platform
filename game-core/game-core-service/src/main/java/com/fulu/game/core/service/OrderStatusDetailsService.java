package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderStatusDetails;



/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-18 12:04:41
 */
public interface OrderStatusDetailsService extends ICommonService<OrderStatusDetails,Integer>{


     void create(Order order,int minute);

}
