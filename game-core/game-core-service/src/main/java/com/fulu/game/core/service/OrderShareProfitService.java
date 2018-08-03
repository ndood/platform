package com.fulu.game.core.service;

import com.fulu.game.core.entity.ArbitrationDetails;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderShareProfit;
import com.fulu.game.core.entity.vo.SourceOrderVO;

import java.math.BigDecimal;
import java.util.List;


/**
 * @author wangbin
 * @email ${email}
 * @date 2018-07-18 13:32:16
 */
public interface OrderShareProfitService extends ICommonService<OrderShareProfit, Integer> {

    void shareProfit(Order order);

    void orderRefund(Order order, BigDecimal refundMoney);

    /**
     * 订单金额部分退款给用户，部分退款给陪玩师
     *
     * @param order
     * @param details
     */
    void orderRefundToUserAndServiceUser(Order order, ArbitrationDetails details);

    /**
     * 根据陪玩师id获取外链来源的订单汇总信息
     *
     * @param userId 陪玩师id
     * @return 订单汇总信息
     */
    List<SourceOrderVO> getSourceOrderList(Integer userId);
}
