package com.fulu.game.core.service;

import com.fulu.game.core.entity.AutoReceivingOrder;



/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-25 18:48:32
 */
public interface AutoReceivingOrderService extends ICommonService<AutoReceivingOrder,Integer>{


    AutoReceivingOrder addAutoReceivingTech(Integer techAuthId,String remark);
}
