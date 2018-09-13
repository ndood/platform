package com.fulu.game.core.service;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderDeal;
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
    OrderVO payOrder(String orderNo, BigDecimal orderMoney);

    /**
     * 陪玩师开始服务
     *
     * @param orderNo
     * @return
     */
    String serverStartServeOrder(String orderNo);


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
    OrderEventVO findOrderEvent(String orderNo);

    /**
     * 陪玩师拒绝协商订单
     *
     * @param orderNo
     * @param remark
     * @param fileUrls
     * @return
     */
    String consultRejectOrder(String orderNo, int orderDealId, String remark, String[] fileUrls);


    /**
     * 陪玩师同样协商订单
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    String consultAgreeOrder(String orderNo, int orderEventId);


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
     * @param orderNo
     * @return
     */
    String serverCancelOrder(String orderNo);

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
     * @param orderNo
     * @return
     */
    String serverAcceptanceOrder(String orderNo, String remark, String[] fileUrl);

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
