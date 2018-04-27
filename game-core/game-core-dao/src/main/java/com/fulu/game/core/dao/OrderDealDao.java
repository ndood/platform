package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.entity.vo.OrderDealVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-26 17:51:54
 */
@Mapper
public interface OrderDealDao extends ICommonDao<OrderDeal,Integer>{

    List<OrderDeal> findByParameter(OrderDealVO orderDealVO);

}
