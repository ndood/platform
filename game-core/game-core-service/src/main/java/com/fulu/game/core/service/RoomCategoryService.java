package com.fulu.game.core.service;

import com.fulu.game.core.entity.RoomCategory;
import com.fulu.game.core.entity.vo.RoomCategoryVO;

import java.util.List;


/**
 * 房间分类表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-10-07 00:25:52
 */
public interface RoomCategoryService extends ICommonService<RoomCategory,Integer>{


    /**
     * 通过父ID查找激活的房间分类
      * @param pid
     * @return
     */
     List<RoomCategory> findActivateRoomByPid(int pid);

    /**
     * 查找可用的房间分类
     * @return
     */
     List<RoomCategory> findActivateRoomCategory();

    /**
     * 获取根分类
     * @return
     */
     RoomCategory getRootCategory();

    /**
     * 通过pid查询分类
      * @param pid
     * @return
     */
     List<RoomCategory> findByPid(int pid);

    /**
     * 查询所有的房间分类
     * @return
     */
    List<RoomCategory> list();

    /**
     * 查询房间分类并查询房间列表
     * @param roomCategoryList
     * @param userId
     * @return
     */
    List<RoomCategoryVO>  getRoomListCategory(List<RoomCategory> roomCategoryList,Integer userId);

}
