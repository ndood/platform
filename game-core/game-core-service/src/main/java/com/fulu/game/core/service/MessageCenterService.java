package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.RoomAssignOrder;

/**
 * 消息中心
 */
public interface MessageCenterService {


    /**
     * 发送派单消息
     */
     void sendAssignOrderMsg(RoomAssignOrder roomAssignOrder);

    /**
     * 更新派单消息已结束
     * @param roomAssignOrder
     */
    void updateSendAssignOrderMsg(RoomAssignOrder roomAssignOrder);


    /**
     * 陪完师发送订单消息
     */
    void sendServerOrderMsg(Order order, String message);

    /**
     * 发送用户订单消息
     * @param order
     * @param message
     */
    void sendUserOrderMsg(Order order, String message);

}
