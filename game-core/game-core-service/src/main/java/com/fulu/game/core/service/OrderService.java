package com.fulu.game.core.service;

import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.core.entity.ArbitrationDetails;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;
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
     * @param orderNo
     * @param orderEventId
     * @return
     */
    String consultAgreeOrder(String orderNo, int orderEventId);


    /**
     * 取消协商
     * @param orderNo
     * @param orderEventId
     * @return
     */
    String consultCancelOrder(String orderNo, int orderEventId);

    /**
     * 陪玩师取消订单
     * @param orderNo
     * @return
     */
    OrderVO serverCancelOrder(String orderNo);

    /**
     * 用户取消订单
     *
     * @param orderNo
     * @return
     */
    OrderVO userCancelOrder(String orderNo);

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
    OrderVO serverAcceptanceOrder(String orderNo, String remark, String[] fileUrl);

    /**
     * 用户验收订单
     *
     * @param orderNo
     * @return
     */
    OrderVO userVerifyOrder(String orderNo);

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
     * @param serverId
     * @return
     */
    int allOrderCount(Integer serverId);

    /**
     * 留言
     * @param orderNo
     * @param eventId
     * @param remark
     * @param fileUrl
     * @return
     */
    OrderDeal eventLeaveMessage(String orderNo, Integer eventId, String remark, String... fileUrl);

    /**
     * 是否是老用户（下单过的）
     */
    Boolean isOldUser(Integer userId);
}
