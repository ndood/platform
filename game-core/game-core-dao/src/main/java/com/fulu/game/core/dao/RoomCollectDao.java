package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Room;
import com.fulu.game.core.entity.RoomCollect;
import com.fulu.game.core.entity.vo.RoomCollectVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 房间收藏
 * @author wangbin
 * @email ${email}
 * @date 2018-10-09 13:17:47
 */
@Mapper
public interface RoomCollectDao extends ICommonDao<RoomCollect,Integer>{

    List<RoomCollect> findByParameter(RoomCollectVO roomCollectVO);



}
