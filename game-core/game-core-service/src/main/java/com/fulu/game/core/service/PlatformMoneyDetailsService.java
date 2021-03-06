package com.fulu.game.core.service;

import com.fulu.game.common.enums.PlatFormMoneyTypeEnum;
import com.fulu.game.core.entity.PlatformMoneyDetails;

import java.math.BigDecimal;
import java.util.List;


/**
 * 平台流水表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-02 17:10:26
 */
public interface PlatformMoneyDetailsService{

    /**
     * 订单流水操作
     * @param orderNo
     * @param money
     * @return
     */
    PlatformMoneyDetails createOrderDetails(PlatFormMoneyTypeEnum platFormMoneyTypeEnum, String orderNo, BigDecimal money);


    /**
     * 零钱流水操作
     * @param remark
     * @param money
     * @return
     */
    PlatformMoneyDetails createSmallChangeDetails(String remark,Integer userId,BigDecimal money);


    List<PlatformMoneyDetails> findByOrderNo(String orderNo);
}
