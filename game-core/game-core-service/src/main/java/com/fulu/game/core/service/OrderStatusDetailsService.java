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

      void create(String orderNo,Integer orderStatus);

      void resetOrderStatus(String orderNo, Integer orderStatus,Integer[] invalidStatus);

      OrderStatusDetails findByOrderAndStatus(String orderNo, Integer orderStatus);

      long getCountDown(String orderNo,Integer orderStatus);

      List<OrderStatusDetails> findByOrderStatus(String orderNo,List<Integer> statusList);

      /**
       * 获取订单流程
       * @param orderNo
       * @return
       */
      List<OrderStatusDetails> findOrderProcess(String orderNo);

}
