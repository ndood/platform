package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.vo.OrderVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 订单表
 * @author yanbiao
 * @date 2018-04-25 18:27:54
 */
public interface OrderService extends ICommonService<Order,Integer>{


    //todo 非自己的订单不能操作

    PageInfo<OrderVO> userList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr);

    PageInfo<OrderVO> serverList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr);

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
    OrderVO userAppealOrder(String orderNo,String remark,String ... fileUrl);

    /**
     * 订单验收
     * @param orderNo
     * @param remark
     * @param fileUrl
     * @return
     */
    OrderVO serverAcceptanceOrder(String orderNo, String remark, String ... fileUrl);

    /**
     * 用户验收订单
     * @param orderNo
     * @return
     */
    OrderVO userVerifyOrder(String orderNo);

    /**
     * 管理员完成订单 付款给打手
     * @param orderNo
     * @return
     */
    OrderVO adminHandleCompleteOrder(String orderNo);

    /**
     * 管理员完成订单 退款给用户
     * @param orderNo
     * @return
     */
    OrderVO adminHandleRefundOrder(String orderNo);

    /**
     * 通过订单号查找订单
     * @param orderNo
     * @return
     */
    Order findByOrderNo(String orderNo);

    /**
     * 陪玩师是否已经在服务用户
     * @param serverId
     * @return
     */
    Boolean isAlreadyService(Integer serverId);

    List<Order> findByStatusList(Integer[] statusList);

}
