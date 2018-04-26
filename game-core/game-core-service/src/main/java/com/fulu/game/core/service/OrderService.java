package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.vo.OrderVO;


/**
 * 订单表
 * 
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-25 18:27:54
 */
public interface OrderService extends ICommonService<Order,Integer>{

    /**
     * 提交订单
     * @param productId
     * @param num
     * @param remark
     * @return
     */
    OrderVO submit(int productId,int num,String remark);

    /**
     * 支付订单,订单回调的时候调用
     * @param orderNo
     * @return
     */
    OrderVO payOrder(String orderNo);

    /**
     * 陪玩师接收订单
     * @param orderNo
     * @return
     */
    OrderVO serverReceiveOrder(String orderNo);

    /**
     * 陪玩师取消订单
     * @param orderNo
     * @return
     */
    OrderVO serverCancelOrder(String orderNo);

    /**
     * 用户取消订单
     * @param orderNo
     * @return
     */
    OrderVO userCancelOrder(String orderNo);

    /**
     * 订单申诉
     * @param orderNo
     * @return
     */
    OrderVO userAppealOrder(String orderNo);




    Order findByOrderNo(String orderNo);

}
