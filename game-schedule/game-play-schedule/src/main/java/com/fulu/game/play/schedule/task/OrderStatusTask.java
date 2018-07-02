package com.fulu.game.play.schedule.task;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.OrderService;
import com.xiaoleilu.hutool.date.DateUnit;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderStatusTask {

    @Autowired
    private OrderService orderService;

    /**
     * 取消平台超时订单
     */
    @Scheduled(cron = "0 0/10 * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void cancelTimeOutOrder() {
        //todo 处理已付款的订单
        Integer[] statusList = new Integer[]{OrderStatusEnum.NON_PAYMENT.getStatus(), OrderStatusEnum.WAIT_SERVICE.getStatus()};
        List<Order> orderList = orderService.findByStatusListAndType(statusList, OrderTypeEnum.PLATFORM.getType());
        for (Order order : orderList) {
            long hour = DateUtil.between(order.getCreateTime(), new Date(), DateUnit.HOUR);
            if (hour >= 24) {
                log.info(order.getOrderNo() + "-------取消订单!");
                orderService.systemCancelOrder(order.getOrderNo());
            }
        }
    }


    /**
     * 自动完成订单
     */
    @Scheduled(cron = "0 0/15 * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void autoCompleteOrder() {
        Integer[] statusList = new Integer[]{OrderStatusEnum.CHECK.getStatus()};
        List<Order> orderList = orderService.findByStatusList(statusList);
        for (Order order : orderList) {
            long hour = DateUtil.between(order.getCreateTime(), new Date(), DateUnit.HOUR);
            if (hour >= 24) {
                log.info(order.getOrderNo() + "-------订单完成!");
                orderService.systemCompleteOrder(order.getOrderNo());
            }
        }
    }


}
