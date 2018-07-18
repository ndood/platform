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


     public void create(String orderNo,Integer orderStatus, int minute);

     public void resetOrderStatus(String orderNo, Integer orderStatus,int minute);


     public OrderStatusDetails findByOrderAndStatus(String orderNo, Integer orderStatus);



}
