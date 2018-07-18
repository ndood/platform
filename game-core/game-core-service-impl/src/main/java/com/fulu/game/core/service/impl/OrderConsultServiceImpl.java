package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.OrderConsultTypeEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OrderConsultDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderConsult;
import com.fulu.game.core.service.OrderConsultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;


@Service
public class OrderConsultServiceImpl extends AbsCommonService<OrderConsult, Integer> implements OrderConsultService {

    @Autowired
    private OrderConsultDao orderConsultDao;


    @Override
    public ICommonDao<OrderConsult, Integer> getDao() {
        return orderConsultDao;
    }


    @Override
    public OrderConsult createConsult(Order order, int orderStatus, BigDecimal refundMoney) {
        OrderConsult orderConsult = new OrderConsult();
        orderConsult.setOrderNo(order.getOrderNo());
        orderConsult.setRefundMoney(refundMoney);
        orderConsult.setOrderStatus(orderStatus);
        orderConsult.setUserId(order.getUserId());
        orderConsult.setServerUserId(order.getServiceUserId());
        orderConsult.setType(OrderConsultTypeEnum.CONSULT.getType());
        orderConsult.setCreateTime(new Date());
        orderConsult.setIsDel(false);
        create(orderConsult);
        return orderConsult;
    }
}
