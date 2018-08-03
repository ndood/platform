package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderShareProfit;
import com.fulu.game.core.entity.vo.OrderShareProfitVO;
import com.fulu.game.core.entity.vo.SourceOrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author wangbin
 * @email ${email}
 * @date 2018-07-18 13:32:16
 */
@Mapper
public interface OrderShareProfitDao extends ICommonDao<OrderShareProfit, Integer> {

    List<OrderShareProfit> findByParameter(OrderShareProfitVO orderShareProfitVO);

    /**
     * 根据陪玩师id获取外链来源的订单汇总信息
     *
     * @param userId 陪玩师id
     * @return 订单汇总信息列表
     */
    List<SourceOrderVO> getSourceOrderList(Integer userId);
}
