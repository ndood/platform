package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.vo.OrderVO;


/**
 * 订单表
 * 
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-25 18:27:54
 */
public interface OrderService extends ICommonService<Order,Integer>{


    OrderVO submit(int productId,int num,String remark);


     Order findByOrderNo(String orderNo);

}
