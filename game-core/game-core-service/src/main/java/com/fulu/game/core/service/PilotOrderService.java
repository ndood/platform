package com.fulu.game.core.service;

import com.fulu.game.core.entity.PilotOrder;



/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-06-26 14:44:22
 */
public interface PilotOrderService extends ICommonService<PilotOrder,Integer>{



    PilotOrder findByOrderNo(String orderNo);
}
