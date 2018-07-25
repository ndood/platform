package com.fulu.game.core.dao;

import com.fulu.game.core.entity.AutoReceivingOrder;
import com.fulu.game.core.entity.vo.AutoReceivingOrderVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-25 18:48:32
 */
@Mapper
public interface AutoReceivingOrderDao extends ICommonDao<AutoReceivingOrder,Integer>{

    List<AutoReceivingOrder> findByParameter(AutoReceivingOrderVO autoReceivingOrderVO);

}
