package com.fulu.game.play.schedule.task;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.SettingTypeEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.Setting;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.AssignOrderService;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.SettingService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Component
@Slf4j
public class AutoSendOrderTask {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SettingService settingService;
    @Autowired
    private AssignOrderService assignOrderService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    /**
     * 自动派单
     */
    @Scheduled(cron = "0/10 * * * * ? ")  //cron接受cron表达式，根据cron表达式确定定时规则
    public void autoCompleteOrder() {
        log.info("自动派单任务开始---");
        Setting setting = settingService.lastSettingType(SettingTypeEnum.AUTO_RECEIVE_ORDER_TIME.getType());
        long autoReveTimeSecond =  new BigDecimal(setting.getVal()).multiply(new BigDecimal(60)).longValue();
        log.info("自动派单时间为:{}",autoReveTimeSecond);
        Integer[] statusList = new Integer[]{OrderStatusEnum.WAIT_SERVICE.getStatus()};
        List<Order> orderList = orderService.findByStatusListAndType(statusList, OrderTypeEnum.POINT.getType());
        for (Order order : orderList) {
            long apartSecond = DateUtil.between(order.getCreateTime(), new Date(), DateUnit.SECOND);
            log.info("订单时间间隔:apartSecond:{};orderNo:{}",apartSecond,order.getOrderNo());
            if(apartSecond>autoReveTimeSecond){
                User user = null;
                try {
                    user = assignOrderService.getMatchUser(order);
                }catch (Exception e){
                    log.error("查询派单用户失败",e);
                }
                if(user==null){
                    log.info("没有查询到可以接单的用户");
                    continue;
                }
                try {
                    log.info("指派订单:order:{};接单用户user:{}",order,user);
                    orderService.receivePointOrder(order.getOrderNo(),user);
                    redisOpenService.set(RedisKeyEnum.AUTO_ASSIGN_ORDER_USER.generateKey(user.getId()),order.getOrderNo(),30*60);
                }catch (Exception e){
                    log.error("派单失败",e);
                }


            }
        }
    }


}
