package com.fulu.game.core.service;

import com.fulu.game.core.entity.PilotOrderDetails;
import com.fulu.game.core.entity.vo.PilotOrderDetailsVO;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;


/**
 * 领航订单流水表
 * 
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-07-05 14:31:31
 */
public interface PilotOrderDetailsService extends ICommonService<PilotOrderDetails,Integer>{

    /**
     * 打款
     * @param money
     * @param remark
     * @return
     */
    boolean remit(BigDecimal money, String remark);

    /**
     * 打款流水列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<PilotOrderDetailsVO> findDetailsList(Integer pageNum, Integer pageSize);

    /**
     * 获取领航账户余额
     * @return
     */
    BigDecimal leftAmount();
	
}
