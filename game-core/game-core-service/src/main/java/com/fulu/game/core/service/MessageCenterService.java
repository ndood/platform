package com.fulu.game.core.service;

import com.fulu.game.core.entity.RoomAssignOrder;

/**
 * 消息中心
 */
public interface MessageCenterService {


    /**
     * 发送派单消息
     */
     void sendAssignOrderMsg(Integer categoryId, String roomNo, String content,Boolean status, String[] targetImIds);

}
