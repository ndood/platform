package com.fulu.game.core.service;

import com.fulu.game.common.enums.RoomEnum;
import com.fulu.game.core.entity.RoomManage;

import java.util.List;


/**
 * 房间管理表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-10-07 00:25:52
 */
public interface RoomManageService extends ICommonService<RoomManage,Integer>{

    /**
     * 创建房间马甲
     * @param roomRoleTypeEnum
     * @param userId
     * @param roomNo
     * @return
     */
    RoomManage createManage(RoomEnum.RoomRoleTypeEnum roomRoleTypeEnum, int userId, String roomNo);

    /**
     * 删除房间马甲
     * @param userId
     * @param roomNo
     */
    void deleteManage(int userId, String roomNo);

    /**
     * 通过用户和房间号查找马甲
     * @param userId
     * @param roomNo
     * @return
     */
    RoomManage findByUserAndRoomNo(int userId,String roomNo);

    /**
     * 聊天室马甲用户列表
     * @param roomNo
     * @return
     */
    List<RoomManage> findByRoomNo(String roomNo);

}
