package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.OrderConsultTypeEnum;
import com.fulu.game.common.enums.OrderDealTypeEnum;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OrderConsultDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderConsult;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.service.OrderConsultService;
import com.fulu.game.core.service.OrderDealService;
import com.fulu.game.core.service.OrderStatusDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;


@Service
public class OrderConsultServiceImpl extends AbsCommonService<OrderConsult, Integer> implements OrderConsultService {

    @Autowired
    private OrderConsultDao orderConsultDao;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;
    @Autowired
    private OrderDealService orderDealService;

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
        orderConsult.setServiceUserId(order.getServiceUserId());
        orderConsult.setType(OrderConsultTypeEnum.CONSULT.getType());
        orderConsult.setCreateTime(new Date());
        orderConsult.setIsDel(false);
        create(orderConsult);
        return orderConsult;
    }

    //取消协商
    @Override
    public void cancelConsult(Order order, Integer userId,OrderConsult orderConsult) {
        //创建取消协商留言
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle("取消协商");
        orderDeal.setType(OrderDealTypeEnum.CONSULT.getType());
        orderDeal.setUserId(userId);
        orderDeal.setRemark("用户取消协商订单");
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderConsultId(orderConsult.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal,null);
        //创建取消协商的订单状态详情
        orderStatusDetailsService.create(order.getOrderNo(), OrderStatusEnum.CONSULT_CANCEL.getStatus(),0);
        //重置订单状态
        orderStatusDetailsService.resetOrderStatus(order.getOrderNo(),orderConsult.getOrderStatus(),72*60);
        //删除申诉
        orderConsult.setIsDel(true);
        update(orderConsult);
    }




}
