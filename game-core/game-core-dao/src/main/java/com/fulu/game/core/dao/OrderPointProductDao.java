package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderPointProduct;
import com.fulu.game.core.entity.vo.OrderPointProductVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 上分订单详情
 * @author wangbin
 * @email ${email}
 * @date 2018-07-24 17:55:45
 */
@Mapper
public interface OrderPointProductDao extends ICommonDao<OrderPointProduct,Integer>{

    List<OrderPointProduct> findByParameter(OrderPointProductVO orderPointProductVO);

}
