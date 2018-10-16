package com.fulu.game.core.service;

import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.entity.vo.VirtualPayOrderVO;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;


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

    /**
     * 订单成功支付
     *
     * @param orderNo
     * @param actualMoney
     * @return
     */
    VirtualPayOrder successPayOrder(String orderNo, BigDecimal actualMoney);

    /**
     * 余额充值
     *
     * @param userId
     * @param money
     * @param payment
     * @param payPath
     * @param ip
     * @return
     */
    VirtualPayOrder balanceCharge(Integer userId, BigDecimal money, Integer payment, Integer payPath, String ip);


    VirtualPayOrder diamondCharge(Integer userId,Long virtualMoney, Integer payment,Integer payPath,String ip );




    VirtualPayOrder findByOrderNo(String orderNo);

    PageInfo<VirtualPayOrderVO> chargeList(VirtualPayOrderVO payOrderVO, Integer pageNum, Integer pageSize, String orderBy);
}
