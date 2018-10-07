package com.fulu.game.core.service;

import com.fulu.game.core.entity.Room;
import com.fulu.game.core.entity.vo.RoomVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.List;


/**
 * 聊天室
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-10-07 00:25:52
 */
public interface RoomService extends ICommonService<Room,Integer>{


    /**
     * 查找可用并且热门的房间
     * @return
     */
    PageInfo<RoomVO> findUsableRoomsByHot(int pageNum,int pageSize);


    /**
     * 按照房间分类查找房间
     * @return
     */
    PageInfo<RoomVO> findUsableRoomsByRoomCategory(int roomCategoryId);


    /**
     * 房主房间
     * @param userId
     * @return
     */
    RoomVO findByOwner(int userId);
	
}
