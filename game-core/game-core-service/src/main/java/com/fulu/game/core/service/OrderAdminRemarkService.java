package com.fulu.game.core.service;

import com.fulu.game.core.entity.OrderAdminRemark;



/**
 * 
 * 
 * @author jaydee.Deng
 * @email ${email}
 * @date 2018-09-17 16:42:51
 */
public interface OrderAdminRemarkService extends ICommonService<OrderAdminRemark,Integer>{
	
    
    void saveAdminOrderRemark(OrderAdminRemark oar);
    
}
