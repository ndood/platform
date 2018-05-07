package com.fulu.game.play.schedule.task;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.PayService;
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
    @Autowired
    private PayService payService;
    /**
     * 取消超时订单
     */
    @Scheduled(cron = "0/1 0/10 * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void cancelTimeOutOrder() {
        //todo 处理已付款的订单
        Integer[] statusList = new Integer[]{OrderStatusEnum.NON_PAYMENT.getStatus(),OrderStatusEnum.WAIT_SERVICE.getStatus()};
        List<Order> orderList = orderService.findByStatusList(statusList);
        for(Order order : orderList){
            long hour = DateUtil.between(order.getCreateTime(),new Date(),DateUnit.HOUR);
            if(hour>=1){
                log.info(order.getOrderNo()+"-------取消订单!");
                order.setStatus(OrderStatusEnum.SYSTEM_CLOSE.getStatus());
                order.setUpdateTime(new Date());
                orderService.update(order);
                if(order.getIsPay()){
                    try {
                        payService.refund(order.getOrderNo(),order.getTotalMoney());
                    }catch (Exception e){
                        log.error("退款失败{}",order.getOrderNo(),e.getMessage());
                    }

                }
            }
        }
    }


    /**
     * 自动完成订单
     */
    @Scheduled(cron = "0/1 0/15 * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void autoCompleteOrder() {
        Integer[] statusList = new Integer[]{OrderStatusEnum.CHECK.getStatus()};
        List<Order> orderList = orderService.findByStatusList(statusList);
        for(Order order : orderList){
            long hour = DateUtil.between(order.getCreateTime(),new Date(),DateUnit.HOUR);
            if(hour>=1){
                log.info(order.getOrderNo()+"-------订单完成!");
                order.setStatus(OrderStatusEnum.SYSTEM_COMPLETE.getStatus());
                order.setUpdateTime(new Date());
                order.setCompleteTime(new Date());
                orderService.update(order);
                orderService.shareProfit(order);
            }
        }
    }




}
