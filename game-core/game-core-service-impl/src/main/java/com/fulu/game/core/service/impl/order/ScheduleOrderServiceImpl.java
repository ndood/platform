package com.fulu.game.core.service.impl.order;

import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.core.dao.OrderDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.entity.OrderEvent;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.OrderDealService;
import com.fulu.game.core.service.OrderEventService;
import com.fulu.game.core.service.OrderShareProfitService;
import com.fulu.game.core.service.OrderStatusDetailsService;
import com.fulu.game.core.service.impl.OrderServiceImpl;
import com.fulu.game.core.service.impl.profit.PlayOrderShareProfitServiceImpl;
import com.fulu.game.core.service.impl.profit.PointOrderShareProfitServiceImpl;
import com.fulu.game.core.service.impl.push.SchedulePushServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.fulu.game.common.enums.OrderStatusEnum.NON_PAYMENT;

@Service
@Slf4j
public class ScheduleOrderServiceImpl extends OrderServiceImpl {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderDealService orderDealService;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;

    @Autowired
    private OrderEventService orderEventService;
    @Autowired
    private PlayOrderShareProfitServiceImpl playOrderShareProfitService;
    @Autowired
    private PointOrderShareProfitServiceImpl pointOrderShareProfitService;
    @Autowired
    private SchedulePushServiceImpl schedulePushService;

    @Override
    protected void dealOrderAfterPay(Order order) {
    }



    @Override
    protected void shareProfit(Order order) {
        if (OrderTypeEnum.PLATFORM.getType().equals(order.getType())) {
            //陪玩订单
            pointOrderShareProfitService.shareProfit(order);
        } else if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            //上分订单
            playOrderShareProfitService.shareProfit(order);
        }
    }

    @Override
    public void orderRefund(Order order, BigDecimal refundMoney) {
        if (order == null) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_NOT_EXIST, "");
        }
        if (order.getType().equals(1)) {
            //陪玩订单
            playOrderShareProfitService.orderRefund(order, refundMoney);
        } else if (order.getType().equals(2)) {
            //上分订单
            pointOrderShareProfitService.orderRefund(order, refundMoney);
        }
    }


    public List<Order> findByStatusListAndType(Integer[] statusList, int type) {
        if (statusList == null) {
            return new ArrayList<>();
        }
        OrderVO param = new OrderVO();
        param.setStatusList(statusList);
        param.setType(type);
        return orderDao.findByParameter(param);
    }

    /**
     * 系统完成订单
     *
     * @param orderNo
     * @return
     */
    public void systemCompleteOrder(String orderNo) {
        Order order = findByOrderNo(orderNo);
        log.info("系统开始完成订单order:{}", order);
        if (!order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有待验收订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.SYSTEM_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        //订单分润
        shareProfit(order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        log.info("系统完成订单order:{}", order);
    }

    /**
     * 系统取消订单
     *
     * @param orderNo
     */
    public void systemCancelOrder(String orderNo) {
        Order order = findByOrderNo(orderNo);
        log.info("系统取消订单开始order:{}", order);
        if (!order.getStatus().equals(NON_PAYMENT.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.ALREADY_RECEIVING.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有等待陪玩和未支付的订单才能取消!");
        }
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        // 全额退款用户
        if (order.getIsPay()) {
            order.setStatus(OrderStatusEnum.SYSTEM_CLOSE.getStatus());
            orderRefund(order, order.getActualMoney());
        } else {
            order.setStatus(OrderStatusEnum.UNPAY_ORDER_CLOSE.getStatus());
        }
        update(order);
        log.info("系统取消订单完成order:{}", order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
    }

    /**
     * 系统自动同意协商
     * @param orderNo
     * @return
     */
    public void systemConsultAgreeOrder(String orderNo) {
        log.info("陪玩师同意协商处理订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        OrderEvent orderEvent = orderEventService.findByOrderNoAndType(orderNo, OrderEventTypeEnum.CONSULT.getType());
        if (!order.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        order.setStatus(OrderStatusEnum.SYSTEM_CONSULT_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        String title = "系统自动同意了协商，￥" + orderEvent.getRefundMoney().toPlainString() + "已经退款结算";
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle(title);
        orderDeal.setType(OrderDealTypeEnum.CONSULT.getType());
        orderDeal.setUserId(order.getServiceUserId());
        orderDeal.setRemark("系统自动同意协商");
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal);
        //创建订单状态详情
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //推送通知同意协商
        schedulePushService.consultAgree(order);
        //退款给用户
        orderRefund(order, orderEvent.getRefundMoney());
    }

    /**
     * 系统自动取消协商
     * @param orderNo
     * @return
     */
    public void systemConsultCancelOrder(String orderNo) {
        log.info("取消协商处理订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        OrderEvent orderEvent = orderEventService.findByOrderNoAndType(orderNo, OrderEventTypeEnum.CONSULT.getType());
        if (orderEvent == null || !order.getOrderNo().equals(orderEvent.getOrderNo())) {
            throw new OrderException(orderNo, "拒绝协商订单不匹配!");
        }
        if (!order.getStatus().equals(OrderStatusEnum.CONSULT_REJECT.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        orderEventService.cancelConsult(order, null, orderEvent);
        log.info("取消协商处理更改订单状态前:{}", order);
        //订单状态重置,时间重置
        order.setStatus(orderEvent.getOrderStatus());
        order.setUpdateTime(new Date());
        update(order);
        log.info("取消协商处理更改订单状态后:{}", order);
        //推送通知
        schedulePushService.consultCancel(order);
    }


}
