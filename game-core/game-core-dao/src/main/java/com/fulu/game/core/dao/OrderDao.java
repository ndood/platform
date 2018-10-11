package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.vo.OrderDetailsVO;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.entity.vo.PointOrderDetailsVO;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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


    List<OrderResVO> list(OrderSearchVO orderSearchVO);

    List<Order> findBySearchVO(OrderSearchVO orderSearchVO);

    List<OrderDetailsVO> listOrderDetails(@Param(value = "type") Integer type, @Param(value = "userId") Integer userId, @Param(value = "statusList") List<Integer> statusList);

    /**
     * 分期乐订单列表
     *
     * @param userId 老板id
     * @return 订单vo列表
     */
    List<OrderDetailsVO> fenqileOrderList(@Param(value = "userId") Integer userId);

    List<PointOrderDetailsVO> pointOrderList(@Param(value = "type") Integer type, @Param(value = "userId") Integer userId);


    List<PointOrderDetailsVO> receivingPointOrderList(@Param(value = "statusList") List<Integer> statusList);

    /**
     * 上分订单--未接单订单列表
     *
     * @param orderSearchVO 查询VO
     * @return 订单列表
     */
    List<Order> unacceptOrderList(OrderSearchVO orderSearchVO);

    /**
     * 获取当天的对账状态订单
     *
     * @param orderSearchVO
     * @return
     */
    List<Order> findDayReconOrders(OrderSearchVO orderSearchVO);

    List<Order> getBannerOrderList(@Param(value = "authUserId") Integer authUserId, @Param(value = "bossUserId") Integer bossUserId, @Param(value = "statusList") List<Integer> statusList);

    OrderVO findMoneySum(OrderVO orderVO);
}
