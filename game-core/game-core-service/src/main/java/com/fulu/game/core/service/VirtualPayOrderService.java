package com.fulu.game.core.service;

import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualPayOrder;

import java.math.BigDecimal;


/**
 * 虚拟货币充值订单表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-09-03 16:10:17
 */
public interface VirtualPayOrderService extends ICommonService<VirtualPayOrder, Integer> {

    VirtualPayOrder charge(String code, BigDecimal actualMoney, Integer virtualMoney, String mobile);

    VirtualPayOrder findByOrderNo(String orderNo);

    String submit(String sessionkey, BigDecimal actualMoney, Integer virtualMoney, String ip);

    Object pay(String orderNo, String ip);

    Object payOrder(VirtualPayOrder order, User user, String requestIp);
}
