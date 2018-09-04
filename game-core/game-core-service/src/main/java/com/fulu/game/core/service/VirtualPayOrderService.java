package com.fulu.game.core.service;

import com.fulu.game.core.entity.VirtualPayOrder;
import me.chanjar.weixin.common.exception.WxErrorException;

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

}
