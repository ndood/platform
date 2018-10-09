package com.fulu.game.core.service;

import com.fulu.game.core.entity.Room;
import com.fulu.game.core.entity.vo.RoomVO;
import com.github.pagehelper.PageInfo;


/**
 * 聊天室
 *
 * @author wangbin
 * @email ${email}
 * @date 2018-10-07 00:25:52
 */
public interface RoomService extends ICommonService<Room, Integer> {


    /**
     * 查找可用并且热门的房间
     *
     * @return
     */
    PageInfo<RoomVO> findUsableRoomsByHot(int pageNum, int pageSize);


    /**
     * 房间列表
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    PageInfo<RoomVO> list(int pageNum, int pageSize, String name);



    /**
     * 按照房间分类查找房间
     *
     * @return
     */
    PageInfo<RoomVO> findUsableRoomsByRoomCategory(int pageNum, int pageSize,int roomCategoryId);


    /**
     * 用户收藏房间列表
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    PageInfo<RoomVO>  findCollectRoomByUser(int pageNum,int pageSize,int userId);

    /**
     * 房主房间
     * @param userId
     * @return
     */
    RoomVO findByOwner(int userId);

    /**
     * 查找用户的房间
     *
     * @param userId
     * @return
     */
    Room findByUser(int userId);


    /**
     * 保存房间设置
     * @param roomVO
     * @return
     */
    RoomVO save(RoomVO roomVO);

    /**
     * 通过房间号查找房间
     * @param roomNo
     * @return
     */
    Room findByRoomNo(String roomNo);
}
