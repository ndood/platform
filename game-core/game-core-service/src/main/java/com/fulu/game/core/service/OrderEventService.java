package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderEvent;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.OrderEventVO;

import java.math.BigDecimal;


/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-18 15:40:34
 */
public interface OrderEventService extends ICommonService<OrderEvent,Integer>{


    OrderEvent createConsult(Order order, User applyUser, int orderStatus, BigDecimal refundMoney);

    void cancelConsult(Order order, User applyUser,OrderEvent orderConsult);

    /**
     * 陪玩师提交验收订单
     * @param order
     * @param remark
     * @param fileUrl
     * @return
     */
    OrderEvent createCheckEvent(Order order, User applyUser,String remark,String... fileUrl);


    OrderEventVO getOrderEvent(Order order, User user, Integer type);
}
