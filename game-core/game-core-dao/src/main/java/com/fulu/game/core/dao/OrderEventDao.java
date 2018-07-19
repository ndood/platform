package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderEvent;
import com.fulu.game.core.entity.vo.OrderEventVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-18 15:40:34
 */
@Mapper
public interface OrderEventDao extends ICommonDao<OrderEvent,Integer>{

    List<OrderEvent> findByParameter(OrderEventVO orderConsultVO);

}
