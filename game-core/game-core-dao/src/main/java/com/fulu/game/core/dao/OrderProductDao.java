package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderProduct;
import com.fulu.game.core.entity.vo.OrderProductVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 订单技能关联表
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-25 18:27:54
 */
@Mapper
public interface OrderProductDao extends ICommonDao<OrderProduct,Integer>{

    List<OrderProduct> findByParameter(OrderProductVO orderProductVO);

}
