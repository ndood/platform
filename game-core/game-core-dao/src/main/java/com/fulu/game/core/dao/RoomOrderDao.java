package com.fulu.game.core.dao;

import com.fulu.game.core.entity.RoomOrder;
import com.fulu.game.core.entity.bo.RoomOrderBO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天室产生的订单表
 * @author wangbin
 * @email ${email}
 * @date 2018-10-14 15:22:05
 */
@Mapper
public interface RoomOrderDao extends ICommonDao<RoomOrder,Integer>{

    List<RoomOrder> findByParameter(RoomOrderBO roomOrderBO);

}
