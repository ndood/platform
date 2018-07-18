package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderShareProfit;

import java.util.List;

import com.fulu.game.core.entity.vo.OrderShareProfitVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-18 13:32:16
 */
@Mapper
public interface OrderShareProfitDao extends ICommonDao<OrderShareProfit,Integer>{

    List<OrderShareProfit> findByParameter(OrderShareProfitVO orderShareProfitVO);

}
