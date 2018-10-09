package com.fulu.game.core.service;

import com.fulu.game.common.enums.RoomRoleTypeEnum;
import com.fulu.game.core.entity.Room;
import com.fulu.game.core.entity.RoomManage;
import com.fulu.game.core.entity.User;


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
    RoomManage createManage(RoomRoleTypeEnum roomRoleTypeEnum, int userId, String roomNo);

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

}
