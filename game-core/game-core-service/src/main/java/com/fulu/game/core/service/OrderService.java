package com.fulu.game.core.service;

import com.fulu.game.core.entity.ArbitrationDetails;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderMarketProduct;
import com.fulu.game.core.entity.vo.MarketOrderVO;
import com.fulu.game.core.entity.vo.OrderDetailsVO;
import com.fulu.game.core.entity.vo.OrderStatusDetailsVO;
import com.fulu.game.core.entity.vo.OrderVO;
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
     * 管理员订单列表
     *
     * @param orderSearchVO
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    PageInfo<OrderResVO> list(OrderSearchVO orderSearchVO, Integer pageNum, Integer pageSize, String orderBy);


    /**
     * 小程序订单列表
     * @param pageNum
     * @param pageSize
     * @param type
     * @return
     */
    PageInfo<OrderDetailsVO> list(int pageNum, int pageSize,Integer type);



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
     * 集市订单列表
     *
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @param statusArr
     * @return
     */
    PageInfo<MarketOrderVO> marketList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr);

    /**
     * 集市订单抢单
     *
     * @param orderNo
     * @return
     */
    Order marketReceiveOrder(String orderNo);

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
     * 提交集市订单
     *
     * @param channelId
     * @param orderMarketProduct
     * @param remark
     * @param orderIp
     * @return
     */
    String submitMarketOrder(int channelId, OrderMarketProduct orderMarketProduct, String remark, String orderIp, String series);

    /**
     * 支付订单,订单回调的时候调用
     *
     * @param orderNo
     * @return
     */
    OrderVO payOrder(String orderNo, BigDecimal orderMoney);

    /**
     * 陪玩师接收订单
     *
     * @param orderNo
     * @return
     */
    String serverReceiveOrder(String orderNo);

    /**
     * 陪玩师开始服务
     * @param orderNo
     * @return
     */
    String serverStartServeOrder(String orderNo);


    /**
     * 用户协商订单
     * @param orderNo
     * @return
     */
    String userConsultOrder(String orderNo, BigDecimal refundMoney, String remark, String[] fileUrl);

    /**
     * 陪玩师拒绝协商订单
     * @param orderNo
     * @param remark
     * @param fileUrls
     * @return
     */
    String serverRejectConsultOrder(String orderNo, int orderDealId, String remark, String[] fileUrls);


    /**
     * 陪玩师同样协商订单
     * @param orderNo
     * @param orderDealId
     * @param remark
     * @return
     */
    String serverAgreeConsultOrder(String orderNo,int orderDealId,String remark);


    /**
     * 取消协商
     * @param orderNo
     * @param orderDealId
     * @return
     */
    String cancelConsultOrder(String orderNo, int orderDealId);



    /**
     * 陪玩师取消订单
     *
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
    OrderVO userAppealOrder(String orderNo, String remark, String... fileUrl);





    /**
     * 管理员订单申诉
     *
     * @param orderNo
     * @return
     */
    OrderVO adminAppealOrder(String orderNo, String remark);

    /**
     * 订单验收
     *
     * @param orderNo
     * @return
     */
    OrderVO serverAcceptanceOrder(String orderNo,String remark,String[] fileUrl);

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
     *
     * @param serverId
     * @return
     */
    int allOrderCount(Integer serverId);



    /**
     * 用户订单详情页
     *
     * @param orderNo
     * @return
     */
    OrderVO findUserOrderDetails(String orderNo);

    /**
     * 查询订单详情
     * @param orderNo
     * @return
     */
    OrderDetailsVO findOrderDetails(String orderNo);

    /**
     * 陪玩师
     *
     * @param orderNo
     * @return
     */
    OrderVO findServerOrderDetails(String orderNo);

    /**
     * 是否是老用户（下单过的）
     */
    Boolean isOldUser(Integer userId);

    /**
     * 统计渠道商总下单数
     *
     * @param channelId
     * @return
     */
    int countByChannelId(Integer channelId);

    /**
     * 统计渠道商成功订单数
     *
     * @param channelId
     * @return
     */
    int countByChannelIdSuccess(Integer channelId);

    /**
     * 获取订单流程
     * @param orderNo
     * @return
     */
    List<OrderStatusDetailsVO> getOrderProcess(String orderNo);

}
