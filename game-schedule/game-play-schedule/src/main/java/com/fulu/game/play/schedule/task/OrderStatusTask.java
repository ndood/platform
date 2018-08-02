package com.fulu.game.play.schedule.task;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.OrderStatusDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderStatusTask {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;

    /**
     * 取消平台超时订单
     */
    @Scheduled(cron = "0 0/1 * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void cancelTimeOutOrder() {
        //todo 处理已付款的订单
        Integer[] statusList = new Integer[]{OrderStatusEnum.NON_PAYMENT.getStatus(),
                OrderStatusEnum.WAIT_SERVICE.getStatus(),
                OrderStatusEnum.ALREADY_RECEIVING.getStatus()};
        List<Order> orderList = orderService.findByStatusListAndType(statusList, OrderTypeEnum.PLATFORM.getType());
        for (Order order : orderList) {
            long countDown = orderStatusDetailsService.getCountDown(order.getOrderNo(), order.getStatus());
            log.info("订单倒计时orderNo:{};countDown:{}", order.getOrderNo(), countDown);
            if (countDown <= 0) {
                log.info("订单超时时间到order:{}:", order);
                orderService.systemCancelOrder(order.getOrderNo());
            }
        }
    }


    /**
     * 自动完成订单
     */
    @Scheduled(cron = "0 0/1 * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void autoCompleteOrder() {
        Integer[] statusList = new Integer[]{OrderStatusEnum.CHECK.getStatus()};
        List<Order> orderList = orderService.findByStatusList(statusList);
        for (Order order : orderList) {
            long countDown = orderStatusDetailsService.getCountDown(order.getOrderNo(), order.getStatus());
            log.info("订单倒计时orderNo:{};countDown:{}", order.getOrderNo(), countDown);
            if (countDown <= 0) {
                log.info("订单完成时间到order:{}:", order);
                orderService.systemCompleteOrder(order.getOrderNo());
            }
        }
    }


    /**
     * 自动取消拒绝的订单
     */
    @Scheduled(cron = "0 0/1 * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void autoCancelConsultRejectOrder() {
        Integer[] statusList = new Integer[]{OrderStatusEnum.CONSULT_REJECT.getStatus()};
        List<Order> orderList = orderService.findByStatusList(statusList);
        for (Order order : orderList) {
            long countDown = orderStatusDetailsService.getCountDown(order.getOrderNo(), order.getStatus());
            log.info("订单超时时间到order:{}:", order);
            if (countDown <= 0) {
                log.info("系统自动取消协商订单order:{}:", order);
                orderService.systemConsultCancelOrder(order.getOrderNo());
            }
        }
    }


    /**
     * 自动取消协商的订单
     */
    @Scheduled(cron = "0 0/1 * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void autoConsultOrder() {
        Integer[] statusList = new Integer[]{OrderStatusEnum.CONSULTING.getStatus()};
        List<Order> orderList = orderService.findByStatusList(statusList);
        for (Order order : orderList) {
            long countDown = orderStatusDetailsService.getCountDown(order.getOrderNo(), order.getStatus());
            log.info("订单超时时间到order:{}:", order);
            if (countDown <= 0) {
                log.info("系统自动取消协商订单order:{}:", order);
                orderService.systemConsultAgreeOrder(order.getOrderNo());
            }
        }
    }


}
