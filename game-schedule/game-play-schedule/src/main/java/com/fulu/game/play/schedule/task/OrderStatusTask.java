package com.fulu.game.play.schedule.task;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.properties.Config;
import com.fulu.game.common.utils.MailUtil;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.OrderStatusDetailsService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.play.schedule.queue.CollectOrderMailQueue;
import com.fulu.game.schedule.service.impl.ScheduleOrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderStatusTask {

    @Autowired
    private ScheduleOrderServiceImpl scheduleOrderServiceImpl;
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;

    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private CollectOrderMailQueue collectOrderMailQueue;

    /**
     * 取消平台超时订单
     */
    @Scheduled(cron = "0 0/1 * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void cancelTimeOutOrder() {
        Integer[] statusList = new Integer[]{OrderStatusEnum.NON_PAYMENT.getStatus(),
                OrderStatusEnum.WAIT_SERVICE.getStatus(),
                OrderStatusEnum.ALREADY_RECEIVING.getStatus()};
        List<Order> orderList = orderService.findByStatusList(statusList);
        for (Order order : orderList) {
            long countDown = orderStatusDetailsService.getCountDown(order.getOrderNo(), order.getStatus());
            log.info("订单倒计时orderNo:{};countDown:{}", order.getOrderNo(), countDown);
            if (countDown <= 0) {
                log.info("订单超时时间到order:{}:", order);
                scheduleOrderServiceImpl.systemCancelOrder(order.getOrderNo());
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
                scheduleOrderServiceImpl.systemCompleteOrder(order.getOrderNo());
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
                scheduleOrderServiceImpl.systemConsultCancelOrder(order.getOrderNo());
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
                scheduleOrderServiceImpl.systemConsultAgreeOrder(order.getOrderNo());
            }
        }
    }


    /**
     * 轮询查出8分钟未接单的订单，并发送邮件通知给运营
     */
    @Scheduled(cron = "0 0/1 * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void autoSendEmailWaitServiceOrder() {
        List<Order> orderList = orderService.findWaitSendEmailOrder(OrderStatusEnum.WAIT_SERVICE.getStatus(),8);

        
        //筛选订单，并将订单信息加入邮件队列
        for (int i = 0 ; i < orderList.size() ; i++) {
            String checkOrderId = redisOpenService.get(RedisKeyEnum.ORDER_WAITING_SERVICE_ID.generateKey(orderList.get(i).getId()));
            if(StringUtils.isBlank(checkOrderId)){
                collectOrderMailQueue.addOrderInfo(orderList.get(i));
                redisOpenService.set(RedisKeyEnum.ORDER_WAITING_SERVICE_ID.generateKey(orderList.get(i).getId()),String.valueOf(orderList.get(i).getId().intValue()), Constant.ONE_DAY);
            }
        }
        
    }
}
