package com.fulu.game.core.service;

import com.fulu.game.core.entity.PilotOrder;
import com.fulu.game.core.entity.vo.PilotOrderVO;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import com.github.pagehelper.PageInfo;


/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-06-26 14:44:22
 */
public interface PilotOrderService extends ICommonService<PilotOrder,Integer>{



    PilotOrder findByOrderNo(String orderNo);

    public PageInfo<PilotOrderVO> findVoList(int pageNum,
                                             int pageSize,
                                             String orderBy,
                                             OrderSearchVO orderSearchVO);
}
