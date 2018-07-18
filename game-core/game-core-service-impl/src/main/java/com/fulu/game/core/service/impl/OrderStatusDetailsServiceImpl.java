package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderStatusDetailsDao;
import com.fulu.game.core.entity.OrderStatusDetails;
import com.fulu.game.core.service.OrderStatusDetailsService;

import java.util.Date;


@Service
public class OrderStatusDetailsServiceImpl extends AbsCommonService<OrderStatusDetails,Integer> implements OrderStatusDetailsService {

    @Autowired
	private OrderStatusDetailsDao orderStatusDetailsDao;

    @Override
    public ICommonDao<OrderStatusDetails, Integer> getDao() {
        return orderStatusDetailsDao;
    }


    @Override
    public void create(Order order, int minute) {
        OrderStatusDetails orderStatusDetails = new OrderStatusDetails();
        orderStatusDetails.setOrderStatus(order.getStatus());
        orderStatusDetails.setCountDownMinute(minute);
        orderStatusDetails.setOrderNo(order.getOrderNo());
        orderStatusDetails.setTriggerTime(new Date());
        orderStatusDetails.setIsValid(true);
        orderStatusDetails.setCreateTime(new Date());
        orderStatusDetails.setUpdateTime(new Date());
        create(orderStatusDetails);
    }


}
