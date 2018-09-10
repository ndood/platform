package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;

import java.util.Date;
import java.util.List;

/**
 * 订单表
 *
 * @author yanbiao
 * @date 2018-04-25 18:27:54
 */
public interface OrderService extends ICommonService<Order, Integer> {


    /**
     * 通过订单号查找订单
     *
     * @param orderNo
     * @return
     */
    Order findByOrderNo(String orderNo);

    /**
     * 查找状态
     *
     * @param statusList
     * @return
     */
    List<Order> findByStatusList(Integer[] statusList);

    /**
     * 查找指定日期区间和状态的订单数
     *
     * @param statusList
     * @param startTime
     * @param endTime
     * @return
     */
    int count(Integer serverId, Integer[] statusList, Date startTime, Date endTime);

    /**
     * 一周完成订单数
     *
     * @param serverId
     * @return
     */
    int weekOrderCount(Integer serverId);

    /**
     * 用户所有完成订单数
     *
     * @param serverId
     * @return
     */
    int allOrderCount(Integer serverId);


    /**
     * 是否是老用户（下单过的）
     */
    Boolean isOldUser(Integer userId);


    /**
     * 获取需要发送邮件的订单
     *
     * @return
     */
    List<Order> findWaitSendEmailOrder(Integer status , Integer waitMins);

}
