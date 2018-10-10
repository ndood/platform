package com.fulu.game.core.service;

import com.fulu.game.core.entity.Room;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.RoomMicVO;
import com.fulu.game.core.entity.vo.RoomVO;
import com.fulu.game.core.entity.vo.UserChatRoomVO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Set;


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

    /**
     * 聊天室对象转成vo对象
     * @param room
     * @return
     */
    RoomVO room2VO(Room room);

    /**
     * 用户进入聊天室
     * @param user
     * @param roomNo
     * @param password
     * @return
     */
    UserChatRoomVO userEnterChatRoom(User user, String roomNo, String password);

    /**
     * 用户退出聊天室
     * @param userId
     * @param roomNo
     * @return
     */
    long userQuitChatRoom(Integer userId, String roomNo);

    /**
     * 获取聊天室在线用户
     * @param roomNo
     * @return
     */
    List<UserChatRoomVO> getOnlineUser(String roomNo);

    /**
     * 获取房间马甲用户列表
     * @param roomNo
     * @return
     */
    List<UserChatRoomVO> roomMangerList(String roomNo);

    /**
     * 更新用户进入的房间信息
     * @param user
     * @param roomNo
     * @return
     */
    UserChatRoomVO setUserRoomInfo(User user, String roomNo);

    /**
     * 获取用户在聊天室信息
     * @return
     */
    UserChatRoomVO getUserRoomInfo(Integer userId);

    /**
     * 房间所有麦位信息
     * @param roomNo
     * @return
     */
    List<RoomMicVO>  roomMicAll(String roomNo);


    /**
     * 抱用户上麦或者自己上麦
     * @param roomNo
     * @param index
     * @param userId
     * @return
     */
    RoomMicVO  roomMicHoldUp(String roomNo,int index,int userId);


    /**
     * 抱用户下麦或者设置麦位状态
     * @param roomNo
     * @param index
     * @return
     */
    RoomMicVO  roomMicStatus(String roomNo,int index,int status);




}
