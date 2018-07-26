package com.fulu.game.core.service;

import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.to.OrderPointProductTO;
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
     * 向陪玩师推送消息
     * @param order
     * @param wechatTemplateMsgEnum
     */
    void pushToServiceOrderWxMessage(Order order,WechatTemplateMsgEnum wechatTemplateMsgEnum);

    /**
     * 向用户推送消息
     * @param order
     * @param wechatTemplateMsgEnum
     */
    void pushToUserOrderWxMessage(Order order,WechatTemplateMsgEnum wechatTemplateMsgEnum);


    /**
     * 管理员订单列表
     * @param orderSearchVO
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    PageInfo<OrderResVO> list(OrderSearchVO orderSearchVO, Integer pageNum, Integer pageSize, String orderBy);


    /**
     * 小程序订单列表
     *
     * @param pageNum
     * @param pageSize
     * @param type
     * @return
     */
    PageInfo<OrderDetailsVO> list(int pageNum, int pageSize, Integer type);


    /**
     * 用户订单列表
     *
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @param statusArr
     * @return
     */
    PageInfo<OrderVO> userList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr);

    /**
     * 陪玩师订单列表
     *
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @param statusArr
     * @return
     */
    PageInfo<OrderVO> serverList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr);


    /**
     * 上分订单抢单
     * @param orderNo
     * @return
     */
    String receivePointOrder(String orderNo, User serviceUser);

    /**
     * 提交订单
     *
     * @param productId
     * @param num
     * @param remark
     * @return
     */
    String submit(int productId, int num, String remark, String couponNo, String userIp, Integer contactType,
                  String contactInfo);

    /**
     * 领航网吧订单提交
     *
     * @param productId
     * @param num
     * @param remark
     * @param couponNo
     * @param userIp
     * @return
     */
    String pilotSubmit(int productId, int num, String remark, String couponNo, String userIp, Integer contactType,
                       String contactInfo);

    /**
     * 提交上分订单
     * @param orderPointProductVO
     * @param orderIp
     * @return
     */
    String submitPointOrder(OrderPointProductVO orderPointProductVO,
                            String couponNo,
                            Integer contactType,
                            String contactInfo,
                            String orderIp);

    /**
     * 支付订单,订单回调的时候调用
     *
     * @param orderNo
     * @return
     */
    OrderVO payOrder(String orderNo, BigDecimal orderMoney);

    /**
     * 陪玩师接收订单
     * @param orderNo
     * @return
     */
    String serverReceiveOrder(String orderNo);

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
     * 获取协商详情
     * @param orderNo
     * @return
     */
    List<OrderDealVO> findOrderConsultEvent(String orderNo);

    /**
     * 获取仲裁详情
     * @param orderNo
     * @return
     */
    List<OrderDealVO> findNegotiateEvent(String orderNo);

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
     * 系统处理拒绝协商订单
     * @param orderNo
     * @return
     */
    String systemConsultCancelOrder(String orderNo);

    /**
     * 系统处理协商中超时订单
     * @param orderNo
     * @return
     */
    String systemConsultAgreeOrder(String orderNo);

    /**
     * 陪玩师取消订单
     * @param orderNo
     * @return
     */
    OrderVO serverCancelOrder(String orderNo);

    /**
     * 系统取消订单
     *
     * @param orderNo
     * @return
     */
    void systemCancelOrder(String orderNo);

    /**
     * 管理员取消订单
     *
     * @param orderNo
     */
    void adminCancelOrder(String orderNo);

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
     * 系统完成订单
     *
     * @param orderNo
     * @return
     */
    OrderVO systemCompleteOrder(String orderNo);

    /**
     * 用户验收订单
     *
     * @param orderNo
     * @return
     */
    OrderVO userVerifyOrder(String orderNo);

    /**
     * 管理员完成订单 付款给打手
     *
     * @param orderNo
     * @return
     */
    OrderVO adminHandleCompleteOrder(String orderNo);

    /**
     * 管理员完成订单 退款给用户
     *
     * @param orderNo
     * @return
     */
    OrderVO adminHandleRefundOrder(String orderNo);

    /**
     * 管理员协商处理订单(订单金额全部记录平台流水)
     *
     * @param details
     * @return
     */
    OrderVO adminHandleNegotiateOrder(ArbitrationDetails details);

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
     * 普通条件查询
     *
     * @param orderSearchVO
     * @return
     */
    List<Order> findBySearchVO(OrderSearchVO orderSearchVO);

    /**
     * 通过订单状态和类型查询订单
     *
     * @param statusList
     * @return
     */
    List<Order> findByStatusListAndType(Integer[] statusList, int type);

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
     * 查询订单详情
     *
     * @param orderNo
     * @return
     */
    OrderDetailsVO findOrderDetails(String orderNo);


    /**
     * 是否是老用户（下单过的）
     */
    Boolean isOldUser(Integer userId);



    /**
     * 获取订单流程
     *
     * @param orderNo
     * @return
     */
    List<OrderStatusDetailsVO> getOrderProcess(String orderNo);





}
