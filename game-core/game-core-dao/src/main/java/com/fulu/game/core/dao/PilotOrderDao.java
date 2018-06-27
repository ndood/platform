package com.fulu.game.core.dao;

import com.fulu.game.core.entity.PilotOrder;
import com.fulu.game.core.entity.vo.PilotOrderVO;

import java.util.List;

import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-06-26 14:44:22
 */
@Mapper
public interface PilotOrderDao extends ICommonDao<PilotOrder,Integer>{

    List<PilotOrder> findByParameter(PilotOrderVO pilotOrderVO);

    List<PilotOrderVO> findVoList(OrderSearchVO orderSearchVO);


}
