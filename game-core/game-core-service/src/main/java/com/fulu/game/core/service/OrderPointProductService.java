package com.fulu.game.core.service;

import com.fulu.game.core.entity.OrderPointProduct;



/**
 * 上分订单详情
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-24 17:55:45
 */
public interface OrderPointProductService extends ICommonService<OrderPointProduct,Integer>{


    OrderPointProduct findByOrderNo(String orderNo);

}
