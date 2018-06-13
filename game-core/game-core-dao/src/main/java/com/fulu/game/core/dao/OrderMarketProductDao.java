package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderMarketProduct;
import com.fulu.game.core.entity.vo.OrderMarketProductVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 集市订单商品表
 * @author wangbin
 * @email ${email}
 * @date 2018-06-13 17:11:31
 */
@Mapper
public interface OrderMarketProductDao extends ICommonDao<OrderMarketProduct,Integer>{

    List<OrderMarketProduct> findByParameter(OrderMarketProductVO orderMarketProductVO);

}
