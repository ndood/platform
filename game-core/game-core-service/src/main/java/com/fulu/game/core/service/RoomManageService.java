package com.fulu.game.core.service;

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


    RoomManage createOwner(User user,String roomNo);





}
