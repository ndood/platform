package com.fulu.game.core.service;

import java.math.BigDecimal;

/**
 * 余额支付接口
 *
 * @author Gong ZeChun
 * @date 2018/9/13 18:25
 */
public interface BalancePayService {

    /**
     * 余额支付钻石
     * @param userId      用户id
     * @param actualMoney 实付金额
     * @param orderNo     订单编号
     * @return 是否支付成功
     */
    boolean balancePayVirtualMoney(Integer userId, BigDecimal actualMoney, String orderNo);



    boolean balancePayOrder(Integer userId, BigDecimal actualMoney, String orderNo);



}
