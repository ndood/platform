package com.fulu.game.core.service;

import com.fulu.game.core.entity.RoomCollect;
import com.fulu.game.core.entity.vo.RoomVO;
import com.github.pagehelper.PageInfo;


/**
 * 房间收藏
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-10-09 13:17:47
 */
public interface RoomCollectService extends ICommonService<RoomCollect,Integer>{


     RoomCollect findByRoomAndUser(String roomNo,Integer userId);

     RoomCollect userCollect(String roomNo, Integer userId,Boolean flag);
}
