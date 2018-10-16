package com.fulu.game.core.service;

import com.fulu.game.core.entity.RoomAssignOrder;
import com.fulu.game.core.entity.vo.RoomAssignOrderVO;

import java.util.List;


/**
 * 聊天室派单表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-10-14 15:22:05
 */
public interface RoomAssignOrderService extends ICommonService<RoomAssignOrder,Integer>{


    /**
     * 创建一个派单消息
     * @param roomNo
     * @param userId
     * @param categoryId
     * @param content
     */
    void create(String roomNo,Integer userId,Integer categoryId,String content);

    /**
     * 完成房间的派单消息
     * @param roomNo
     * @param userId
     */
    void finishRoomAssignOrder(String roomNo,Integer userId);


    /**
     * 未完成的派单消息列表
     * @param roomNo
     * @param userId
     * @return
     */
    List<RoomAssignOrderVO> unfinishedRoomAssignOrder(String roomNo, Integer userId);



}
