package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.vo.MarketOrderVO;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单表
 *
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-25 18:27:54
 */
@Mapper
public interface OrderDao extends ICommonDao<Order, Integer> {

    List<Order> findByParameter(OrderVO orderVO);

    int countByParameter(OrderVO orderVO);

    List<OrderVO> findVOByParameter(OrderVO orderVO);

    Order findByOrderNo(String orderNo);

    List<MarketOrderVO> findMarketByParameter(OrderVO orderVO);


    List<OrderResVO> list(OrderSearchVO orderSearchVO);

}
