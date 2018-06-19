package com.fulu.game.core.service;

import com.fulu.game.core.entity.OrderMarketProduct;



/**
 * 集市订单商品表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-06-13 17:11:31
 */
public interface OrderMarketProductService extends ICommonService<OrderMarketProduct,Integer>{


    OrderMarketProduct findByOrderNo(String orderNo);
}
