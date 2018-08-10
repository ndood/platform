package com.fulu.game.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.fulu.game.common.enums.OrderEventTypeEnum;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.common.enums.UserScoreEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.OrderDao;
import com.fulu.game.core.dao.OrderEventDao;
import com.fulu.game.core.dao.OrderShareProfitDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.aop.UserScore;
import com.fulu.game.core.service.impl.OrderServiceImpl;
import com.fulu.game.core.service.impl.push.AdminPushServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.fulu.game.common.enums.OrderStatusEnum.NON_PAYMENT;
import static java.math.BigDecimal.ROUND_HALF_DOWN;

@Service
@Slf4j
public class AdminOrderServiceImpl extends OrderServiceImpl {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderProductService orderProductService;
    @Autowired
    private OrderDealService orderDealService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;
    @Autowired
    private OrderShareProfitService orderShareProfitService;
    @Autowired
    private OrderEventService orderEventService;
    @Autowired
    private OrderEventDao orderEventDao;
    @Autowired
    private OrderShareProfitDao orderShareProfitDao;
    @Autowired
    private OrderPointProductService orderPointProductService;
    @Autowired
    private UserAutoReceiveOrderService userAutoReceiveOrderService;
    @Autowired
    private AdminPushServiceImpl adminPushService;

    @Override
    protected void dealOrderAfterPay(Order order) {
    }

    @Override
    protected void shareProfit(Order order) {
    }

    @Override
    protected void orderRefund(Order order, BigDecimal refundMoney) {
    }


    public PageInfo<OrderVO> unacceptOrderList(Integer pageNum, Integer pageSize, OrderSearchVO orderSearchVO) {
        String orderBy = orderSearchVO.getOrderBy();
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "create_time desc";
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<Order> orderList = orderDao.unacceptOrderList(orderSearchVO);
        if (CollectionUtil.isEmpty(orderList)) {
            return null;
        }

        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order meta : orderList) {
            OrderVO orderVO = new OrderVO();
            BeanUtil.copyProperties(meta, orderVO);
            String orderNo = meta.getOrderNo();
            OrderPointProduct orderPointProduct = orderPointProductService.findByOrderNo(orderNo);
            if (orderPointProduct != null) {
                orderVO.setAccountInfo(orderPointProduct.getAccountInfo());
                orderVO.setOrderChoice(orderPointProduct.getOrderChoice());
                orderVO.setPointType(orderPointProduct.getPointType());
            }
            orderVOList.add(orderVO);
        }

        PageInfo page = new PageInfo(orderList);
        page.setList(orderVOList);
        return page;
    }

    public UserAutoReceiveOrderVO getDynamicOrderInfo(Integer userId, Integer categoryId) {
        OrderVO orderVO = new OrderVO();
        orderVO.setUserId(userId);
        List<Order> orderList = orderDao.findByParameter(orderVO);
        if (CollectionUtil.isEmpty(orderList)) {
            return null;
        }
        int runningOrderNum = 0;
        for (Order meta : orderList) {
            boolean flag = meta.getStatus().equals(OrderStatusEnum.ALREADY_RECEIVING.getStatus())
                    || meta.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())
                    || meta.getStatus().equals(OrderStatusEnum.CHECK.getStatus())
                    || meta.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())
                    || meta.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())
                    || meta.getStatus().equals(OrderStatusEnum.APPEALING_ADMIN.getStatus());
            if (flag) {
                runningOrderNum++;
            }
        }

        UserAutoReceiveOrder autoReceiveOrder = userAutoReceiveOrderService.findByUserIdAndCategoryId(userId, categoryId);
        if (autoReceiveOrder == null) {
            return null;
        }

        Integer orderCancelNum = autoReceiveOrder.getOrderCancelNum();
        Integer orderDisputeNum = autoReceiveOrder.getOrderDisputeNum();
        Integer orderNum = autoReceiveOrder.getOrderNum();
        BigDecimal orderFailureRate = new BigDecimal(0);
        if (orderCancelNum != null && orderDisputeNum != null && orderNum != null && orderNum != 0) {
            orderFailureRate = new BigDecimal(orderCancelNum)
                    .add(new BigDecimal(orderDisputeNum))
                    .divide(new BigDecimal(orderNum), 2, ROUND_HALF_DOWN);
        }

        UserAutoReceiveOrderVO resultVo = new UserAutoReceiveOrderVO();
        resultVo.setRunningOrderNum(runningOrderNum);
        resultVo.setOrderFailureRate(orderFailureRate);
        return resultVo;
    }

    public List<OrderStatusDetailsVO> getOrderProcess(String orderNo) {
        List<OrderStatusDetails> detailsList = orderStatusDetailsService.findOrderProcess(orderNo);
        if (CollectionUtil.isEmpty(detailsList)) {
            return null;
        }
        List<OrderStatusDetailsVO> voList = new ArrayList<>();
        for (OrderStatusDetails details : detailsList) {
            String msg = OrderStatusEnum.getMsgByStatus(details.getOrderStatus());
            OrderStatusDetailsVO vo = new OrderStatusDetailsVO();
            vo.setOrderNo(orderNo);
            vo.setCreateTime(details.getCreateTime());
            vo.setOrderStatus(details.getOrderStatus());
            vo.setOrderStatusMsg(msg);
            voList.add(vo);
        }
        return voList;
    }

    public List<Order> findBySearchVO(OrderSearchVO orderSearchVO) {
        Integer status = orderSearchVO.getStatus();
        Integer[] statusList = OrderStatusGroupEnum.getByValue(status);
        if (null != statusList && statusList.length > 0) {
            orderSearchVO.setStatusList(statusList);
        }
        return orderDao.findBySearchVO(orderSearchVO);
    }

    /**
     * 管理员协商处理订单
     *
     * @param details
     * @return
     */
    @UserScore(type = UserScoreEnum.NEGOTIATE)
    public OrderVO adminHandleNegotiateOrder(ArbitrationDetails details) {
        Admin admin = adminService.getCurrentUser();
        String orderNo = details.getOrderNo();
        log.info("管理员协商处理订单orderNo:{};adminId:{};adminName:{};", orderNo, admin.getId(), admin.getName());
        Order order = findByOrderNo(orderNo);
        details.setUserId(order.getUserId());
        details.setServiceUserId(order.getServiceUserId());
        if (!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.APPEALING_ADMIN.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_NEGOTIATE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);

        //仲裁留言
        OrderEvent orderEvent = orderEventService.findByOrderNoAndType(orderNo, OrderEventTypeEnum.APPEAL.getType());
        if (orderEvent != null) {
            OrderDeal orderDeal = new OrderDeal();
            orderDeal.setOrderNo(orderNo);
            orderDeal.setRemark("客服人员仲裁订单，结果:协商处理。留言:" + details.getRemark());
            orderDeal.setTitle("客服人员仲裁订单");
            orderDeal.setType(OrderEventTypeEnum.APPEAL.getType());
            orderDeal.setOrderEventId(orderEvent.getId());
            orderDealService.create(orderDeal);
        }
        adminPushService.appealNegotiate(order, details.getRemark());

        if (order.getIsPay()) {
            orderShareProfitService.orderRefundToUserAndServiceUser(order, details);
            orderStatusDetailsService.create(orderNo, order.getStatus());
        } else {
            throw new OrderException(order.getOrderNo(), "只有已付款的订单才能操作!");
        }
        return orderConvertVo(order);
    }

    /**
     * 管理员退款用户
     *
     * @param orderNo
     * @return
     */
    @UserScore(type = UserScoreEnum.FULL_RESTITUTION)
    public OrderVO adminHandleRefundOrder(String orderNo) {
        Admin admin = adminService.getCurrentUser();
        log.info("管理员退款用户orderNo:{};adminId:{};adminName:{};", orderNo, admin.getId(), admin.getName());
        Order order = findByOrderNo(orderNo);
        if (!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_REFUND.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);

        //仲裁留言
        OrderEvent orderEvent = orderEventService.findByOrderNoAndType(orderNo, OrderEventTypeEnum.APPEAL.getType());
        if (orderEvent != null) {
            OrderDeal orderDeal = new OrderDeal();
            orderDeal.setOrderNo(orderNo);
            orderDeal.setRemark("客服人员仲裁订单，结果:老板胜诉");
            orderDeal.setTitle("客服人员仲裁订单");
            orderDeal.setType(OrderEventTypeEnum.APPEAL.getType());
            orderDeal.setOrderEventId(orderEvent.getId());
            orderDealService.create(orderDeal);
        }
        //推送消息
        adminPushService.appealUserWin(order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        if (order.getIsPay()) {
            orderShareProfitService.orderRefund(order, order.getActualMoney());
        }
        return orderConvertVo(order);
    }

    /**
     * 管理员强制完成订单 (打款给打手)
     *
     * @param orderNo
     * @return
     */
    public OrderVO adminHandleCompleteOrder(String orderNo) {
        Admin admin = adminService.getCurrentUser();
        log.info("管理员强制完成订单 (打款给打手)orderNo:{};adminId:{};adminName:{};", orderNo, admin.getId(), admin.getName());
        Order order = findByOrderNo(orderNo);
        if (!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);

        //仲裁留言
        OrderEvent orderEvent = orderEventService.findByOrderNoAndType(orderNo, OrderEventTypeEnum.APPEAL.getType());
        if (orderEvent != null) {
            OrderDeal orderDeal = new OrderDeal();
            orderDeal.setOrderNo(orderNo);
            orderDeal.setRemark("客服人员仲裁订单，结果:陪玩师胜诉");
            orderDeal.setTitle("客服人员仲裁订单");
            orderDeal.setType(OrderEventTypeEnum.APPEAL.getType());
            orderDeal.setOrderEventId(orderEvent.getId());
            orderDealService.create(orderDeal);
        }
        adminPushService.appealServiceWin(order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //订单分润
        orderShareProfitService.shareProfit(order);
        return orderConvertVo(order);
    }

    /**
     * 管理员取消订单
     *
     * @param orderNo
     */
    public void adminCancelOrder(String orderNo) {
        Admin admin = adminService.getCurrentUser();
        log.info("管理员取消订单orderNo:{};admin:{};", orderNo, admin);
        Order order = findByOrderNo(orderNo);
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())
                && !order.getStatus().equals(NON_PAYMENT.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有陪玩中和等待陪玩的订单才能取消!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_CLOSE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        // 全额退款用户
        if (order.getIsPay()) {
            orderShareProfitService.orderRefund(order, order.getActualMoney());
        }
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 0);
    }

    public List<OrderDealVO> findOrderConsultEvent(String orderNo) {
        return adminFindOrderEvent(orderNo, OrderEventTypeEnum.CONSULT.getType());
    }

    public List<OrderDealVO> findNegotiateEvent(String orderNo) {
        return adminFindOrderEvent(orderNo, OrderEventTypeEnum.APPEAL.getType());
    }

    private List<OrderDealVO> adminFindOrderEvent(String orderNo, Integer orderEventType) {
        Order order = findByOrderNo(orderNo);

        OrderEventVO param = new OrderEventVO();
        param.setOrderNo(order.getOrderNo());
        param.setType(orderEventType);
        List<OrderEvent> orderEventList = orderEventDao.findByParameter(param);
        OrderEvent orderEvent;
        if (CollectionUtil.isEmpty(orderEventList)) {
            return null;
        } else {
            orderEvent = orderEventList.get(0);
        }

        List<OrderDealVO> orderDealVOList = orderDealService.findByOrderEventId(orderEvent.getId());
        if (CollectionUtil.isEmpty(orderDealVOList)) {
            return null;
        }
        for (OrderDealVO orderDealVO : orderDealVOList) {
            User ouser = userService.findById(orderDealVO.getUserId());
            if (ouser != null) {
                orderDealVO.setHeadUrl(ouser.getHeadPortraitsUrl());
                orderDealVO.setNickname(ouser.getNickname());
            }
        }
        return orderDealVOList;
    }

    public PageInfo<OrderResVO> list(OrderSearchVO orderSearchVO, Integer pageNum, Integer pageSize, String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "id DESC";
        }
        if (orderSearchVO.getUserMobile() != null) {
            User user = userService.findByMobile(orderSearchVO.getUserMobile());
            if (user == null) {
                throw new ServiceErrorException("用户手机号输入错误!");
            }
            orderSearchVO.setUserId(user.getId());
        }
        if (orderSearchVO.getServiceUserMobile() != null) {
            User user = userService.findByMobile(orderSearchVO.getServiceUserMobile());
            if (user == null) {
                throw new ServiceErrorException("陪玩师手机号输入错误!");
            }
            orderSearchVO.setServiceUserId(user.getId());
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        Integer status = orderSearchVO.getStatus();
        Integer[] statusList = OrderStatusGroupEnum.getByValue(status);
        if (null != statusList && statusList.length > 0) {
            orderSearchVO.setStatusList(statusList);
        }
        List<OrderResVO> list = orderDao.list(orderSearchVO);
        for (OrderResVO orderResVO : list) {
            //添加订单投诉和验证信息
            OrderDealVO userOrderDealVO = orderDealService.findByUserAndOrderNo(orderResVO.getUserId(), orderResVO.getOrderNo());
            orderResVO.setUserOrderDeal(userOrderDealVO);
            OrderDealVO serviceUserOrderDealVO = orderDealService.findByUserAndOrderNo(orderResVO.getServiceUserId(), orderResVO.getOrderNo());
            orderResVO.setServerOrderDeal(serviceUserOrderDealVO);
            //添加用户和陪玩师信息
            User user = userService.findById(orderResVO.getUserId());
            orderResVO.setUser(user);
            User serviceUser = userService.findById(orderResVO.getServiceUserId());
            orderResVO.setServerUser(serviceUser);
            //添加订单商品信息
            OrderProduct orderProduct = orderProductService.findByOrderNo(orderResVO.getOrderNo());
            orderResVO.setOrderProduct(orderProduct);
//            OrderMarketProduct orderMarketProduct = orderMarketProductService.findByOrderNo(orderResVO.getOrderNo());
//            orderResVO.setOrderMarketProduct(orderMarketProduct);
            //添加订单状态
            orderResVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderResVO.getStatus()));

            OrderShareProfitVO profitVO = new OrderShareProfitVO();
            profitVO.setOrderNo(orderResVO.getOrderNo());
            List<OrderShareProfit> profitList = orderShareProfitDao.findByParameter(profitVO);
            if (CollectionUtil.isEmpty(profitList)) {
                continue;
            }
            OrderShareProfit profit = profitList.get(0);
            BigDecimal commissionMoney = orderResVO.getCommissionMoney();
            BigDecimal serverMoney = orderResVO.getServerMoney();
            if (commissionMoney == null) {
                orderResVO.setCommissionMoney(profit.getCommissionMoney());
            }
            if (serverMoney == null) {
                orderResVO.setServerMoney(profit.getServerMoney());
            }
        }
        return new PageInfo<>(list);
    }


}
