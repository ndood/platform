package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderMoneyDetails;
import com.fulu.game.core.entity.vo.OrderMoneyDetailsVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 订单流水表
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-26 14:07:31
 */
@Mapper
public interface OrderMoneyDetailsDao extends ICommonDao<OrderMoneyDetails,Integer>{

    List<OrderMoneyDetails> findByParameter(OrderMoneyDetailsVO orderMoneyDetailsVO);

}
