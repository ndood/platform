package com.fulu.game.play.schedule.task;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.OrderService;
import com.xiaoleilu.hutool.date.DateUtil;
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

    @Scheduled(cron = "0/1 0/10 * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void testCron() {
        Integer[] statusList = new Integer[]{OrderStatusEnum.NON_PAYMENT.getStatus(),OrderStatusEnum.WAIT_SERVICE.getStatus()};
        List<Order> orderList = orderService.findByStatusList(statusList);
        for(Order order : orderList){
            order.getCreateTime();

//            DateUtil.between()
        }
        log.info("===initialDelay: 第{}次执行方法");
    }


}
