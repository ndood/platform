package com.fulu.game.play.schedule.task;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.properties.Config;
import com.fulu.game.common.utils.MailUtil;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.OrderStatusDetailsService;
import com.fulu.game.schedule.service.impl.ScheduleOrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
    private Config configProperties;

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

        //拼接邮件内容
        StringBuffer mailContent = new StringBuffer();
        mailContent.append("陪玩师8分钟仍然未接单，订单号分别为：");
        for (int i = 0 ; i < orderList.size() ; i++) {

            if(i+1 < orderList.size()){
                mailContent.append(orderList.get(i).getOrderNo()+" ， ");
            }else{
                mailContent.append(orderList.get(i).getOrderNo());
            }
        }

        //发送邮件
        MailUtil.sendMail(configProperties.getOrdermail().getAddress(),configProperties.getOrdermail().getPassword(),"陪玩师8分钟仍然未接单",mailContent.toString(),new String[]{configProperties.getOrdermail().getAddress()});
    }
}
