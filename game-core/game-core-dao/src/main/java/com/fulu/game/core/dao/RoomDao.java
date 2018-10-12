package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Room;
import com.fulu.game.core.entity.vo.RoomVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 聊天室
 * @author wangbin
 * @email ${email}
 * @date 2018-10-07 00:25:52
 */
@Mapper
public interface RoomDao extends ICommonDao<Room,Integer>{

    List<Room> findByParameter(RoomVO roomVO);

    List<Room>  findCollectRoomByUser(@Param("userId")Integer userId);

}
