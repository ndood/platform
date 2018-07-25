package com.fulu.game.play.schedule.task;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.SettingTypeEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.Setting;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.SettingService;
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
public class AutoSendOrderTask {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SettingService settingService;

    /**
     * 自动派单
     */
    @Scheduled(cron = "0 0/1 * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void autoCompleteOrder() {
        Setting setting = settingService.lastSettingType(SettingTypeEnum.AUTO_RECEIVE_ORDER_TIME.getType());
        long autoReveTime = Long.valueOf(setting.getVal());
        Integer[] statusList = new Integer[]{OrderStatusEnum.WAIT_SERVICE.getStatus()};
        List<Order> orderList = orderService.findByStatusListAndType(statusList, OrderTypeEnum.POINT.getType());
        for (Order order : orderList) {
            long minute = DateUtil.between(order.getCreateTime(), new Date(), DateUnit.MINUTE);
            if(minute>autoReveTime){
                //todo
            }
        }
    }


}
