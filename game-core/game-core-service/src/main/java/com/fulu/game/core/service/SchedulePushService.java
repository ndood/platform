package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.PushMsg;

import java.util.List;

/**
 * 定义所有管理后台的推送业务
 */
public interface SchedulePushService {

    /**
     * 同意协商
     *
     * @param order
     */
    void consultAgree(Order order);

    /**
     * 取消协商
     *
     * @param order
     */
    void consultCancel(Order order);

}