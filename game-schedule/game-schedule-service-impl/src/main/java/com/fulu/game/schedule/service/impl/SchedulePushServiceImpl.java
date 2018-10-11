package com.fulu.game.schedule.service.impl;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.push.PushFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchedulePushServiceImpl {

    @Autowired
    private PushFactory pushFactory;

    /**
     * 同意协商
     *
     * @param order
     */
    public void consultAgree(Order order) {
        pushFactory.getPushIns().forEach(schedulePushService ->
                schedulePushService.consultAgree(order)
        );
    }

    /**
     * 取消协商
     *
     * @param order
     */
    public void consultCancel(Order order) {
        pushFactory.getPushIns().forEach(schedulePushService ->
                schedulePushService.consultCancel(order)
        );

    }
}
