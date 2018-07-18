package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderStatusDetails;
import com.fulu.game.core.entity.vo.OrderStatusDetailsVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-18 12:04:41
 */
@Mapper
public interface OrderStatusDetailsDao extends ICommonDao<OrderStatusDetails,Integer>{

    List<OrderStatusDetails> findByParameter(OrderStatusDetailsVO orderStatusDetailsVO);

}
