package com.fulu.game.core.service;

import com.fulu.game.core.entity.OrderStatusDetails;

import java.util.List;


/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-18 12:04:41
 */
public interface OrderStatusDetailsService extends ICommonService<OrderStatusDetails,Integer>{


      void create(String orderNo,Integer orderStatus, int minute);

      void resetOrderStatus(String orderNo, Integer orderStatus,int minute);

      OrderStatusDetails findByOrderAndStatus(String orderNo, Integer orderStatus);

      long getCountDown(String orderNo,Integer orderStatus);

      /**
       * 获取订单流程
       * @param orderNo
       * @return
       */
      List<OrderStatusDetails> findOrderProcess(String orderNo);

}
