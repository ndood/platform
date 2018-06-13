package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderMarketProduct;
import com.fulu.game.core.entity.vo.OrderVO;
import com.github.pagehelper.PageInfo;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单表
 * @author yanbiao
 * @date 2018-04-25 18:27:54
 */
public interface OrderService extends ICommonService<Order,Integer>{


    /**
     * 用户订单列表
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @param statusArr
     * @return
     */
    PageInfo<OrderVO> userList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr);

    /**
     * 陪玩师订单列表
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @param statusArr
     * @return
     */
    PageInfo<OrderVO> serverList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr);

    /**
     * 提交订单
     * @param productId
     * @param num
     * @param remark
     * @return
     */
    OrderVO submit(int productId,int num,String remark,String couponNo,String userIp);

    /**
     * 提交集市订单
     * @param channelId
     * @param orderMarketProduct
     * @param remark
     * @param orderIp
     * @return
     */
    String submitMarketOrder(int channelId, OrderMarketProduct orderMarketProduct, String remark, String orderIp);

    /**
     * 支付订单,订单回调的时候调用
     * @param orderNo
     * @return
     */
     OrderVO payOrder(String orderNo,BigDecimal orderMoney);

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
     * 系统取消订单
     * @param orderNo
     * @return
     */
    OrderVO systemCancelOrder(String orderNo);

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
     * 系统完成订单
     * @param orderNo
     * @return
     */
    OrderVO systemCompleteOrder(String orderNo);
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
     * 管理员协商处理订单(订单金额全部记录平台流水)
     * @param orderNo
     * @return
     */
    OrderVO adminHandleNegotiateOrder(String orderNo);

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

    /**
     * 查找状态
     * @param statusList
     * @return
     */
    List<Order> findByStatusList(Integer[] statusList);

    /**
     * 查找指定日期区间和状态的订单数
     * @param statusList
     * @param startTime
     * @param endTime
     * @return
     */
    int count(Integer serverId,Integer[] statusList,Date startTime,Date endTime);

    /**
     * 一周完成订单数
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
     * 订单分润
     * @param order
     */
    void shareProfit(Order order);

    /**
     * 用户订单详情页
     * @param orderNo
     * @return
     */
    OrderVO findUserOrderDetails(String orderNo);

    /**
     * 陪玩师
     * @param orderNo
     * @return
     */
    OrderVO findServerOrderDetails(String orderNo);

    /**
     * 是否是老用户（下单过的）
     */
    Boolean isOldUser(Integer userId);

}
