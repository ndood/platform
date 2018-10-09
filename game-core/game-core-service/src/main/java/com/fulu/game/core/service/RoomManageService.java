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


    RoomManage createManage(RoomRoleTypeEnum roomRoleTypeEnum, int userId, String roomNo);



    RoomManage findByUserAndRoomNo(int userId,String roomNo);

}
