package com.fulu.game.core.service;

import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.entity.vo.VirtualPayOrderVO;
import com.github.pagehelper.PageInfo;


/**
 * 虚拟币和余额充值订单表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-09-03 16:10:17
 */
public interface VirtualPayOrderService extends ICommonService<VirtualPayOrder, Integer> {

    //    VirtualPayOrder charge(String code, BigDecimal actualMoney, Integer virtualMoney, String mobile);
//
    VirtualPayOrder findByOrderNo(String orderNo);

    PageInfo<VirtualPayOrderVO> chargeList(VirtualPayOrderVO payOrderVO, Integer pageNum, Integer pageSize, String orderBy);
}
