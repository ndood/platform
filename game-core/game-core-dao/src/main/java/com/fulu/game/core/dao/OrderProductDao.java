package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderProduct;
import com.fulu.game.core.entity.vo.OrderProductVO;

import java.util.List;

import com.fulu.game.core.entity.vo.requestVO.OrderReqVO;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单技能关联表
 * @author yanbiao
 * @date 2018-04-25 18:27:54
 */
@Mapper
public interface OrderProductDao extends ICommonDao<OrderProduct,Integer>{

    List<OrderProduct> findByParameter(OrderProductVO orderProductVO);

    List<OrderResVO> findByUnionParam(OrderReqVO orderReqVO);
}
