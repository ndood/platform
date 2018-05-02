package com.fulu.game.core.service;

import com.fulu.game.core.entity.PlatformMoneyDetails;

import java.math.BigDecimal;


/**
 * 平台流水表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-02 17:10:26
 */
public interface PlatformMoneyDetailsService extends ICommonService<PlatformMoneyDetails,Integer>{


    PlatformMoneyDetails createOrderDetails(String orderNo,  BigDecimal money);

}
