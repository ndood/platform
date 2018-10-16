package com.fulu.game.core.service;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.OrderEventVO;
import com.fulu.game.core.entity.vo.OrderVO;
import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;

public interface OrderOpenService {


    /**
     * 支付订单,订单回调的时候调用
     *
     * @param orderNo
     * @return
     */
    OrderVO payOrder(Integer payment, String orderNo, BigDecimal orderMoney);

    /**
     * 陪玩师开始服务
     *
     * @return
     */
    String serverStartServeOrder(Order order);


    /**
     * 用户协商订单
     *
     * @param orderNo
     * @return
     */
    String userConsultOrder(String orderNo, BigDecimal refundMoney, String remark, String[] fileUrl);

    /**
     * 查看留言
     *
     * @param orderNo
     * @return
     */
    OrderEventVO findOrderEvent(String orderNo,Integer currentUserId);

    /**
     * 陪玩师拒绝协商订单
     *
     * @param remark
     * @param fileUrls
     * @return
     */
    String consultRejectOrder(Order order, int orderDealId, String remark, String[] fileUrls , Integer userId);


    /**
     * 陪玩师同样协商订单
     *
     * @param orderEventId
     * @return
     */
    String consultAgreeOrder(Order order, int orderEventId , Integer userId);


    /**
     * 取消协商
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    String consultCancelOrder(String orderNo, int orderEventId);

    /**
     * 陪玩师取消订单
     *
     * @return
     */
    OrderVO serverCancelOrder(Order order);

    /**
     * 用户取消订单
     *
     * @param orderNo
     * @return
     */
    String userCancelOrder(String orderNo);

    /**
     * 用户订单申诉
     *
     * @param orderNo
     * @return
     */
    String userAppealOrder(String orderNo, String remark, String... fileUrl);


    /**
     * 订单验收
     *
     * @param order
     * @return
     */
    public String serverAcceptanceOrder(Order order, String remark, User user,String[] fileUrl);

    /**
     * 用户验收订单
     *
     * @param orderNo
     * @return
     */
    String userVerifyOrder(String orderNo);


    /**
     * 留言
     *
     * @param orderNo
     * @param eventId
     * @param remark
     * @param fileUrl
     * @return
     */
    OrderDeal eventLeaveMessage(String orderNo, Integer eventId, String remark, String... fileUrl);


    /**
     * 获取是否有陪玩师接单状态
     *
     * @return
     */
    DeferredResult<Result> getServiceUserAcceptOrderStatus();


    Order findByOrderNo(String orderNo);


}
