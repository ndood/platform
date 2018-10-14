package com.fulu.game.core.dao;

import com.fulu.game.core.entity.RoomAssignOrder;
import com.fulu.game.core.entity.bo.RoomAssignOrderBO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天室派单表
 * @author wangbin
 * @email ${email}
 * @date 2018-10-14 15:22:05
 */
@Mapper
public interface RoomAssignOrderDao extends ICommonDao<RoomAssignOrder,Integer>{

    List<RoomAssignOrder> findByParameter(RoomAssignOrderBO roomAssignOrderBO);

}
