package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderMsg;
import com.fulu.game.core.entity.vo.OrderMsgVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-09-24 22:35:17
 */
@Mapper
public interface OrderMsgDao extends ICommonDao<OrderMsg,Integer>{

    List<OrderMsg> findByParameter(OrderMsgVO orderMsgVO);

}
