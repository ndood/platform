package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.entity.vo.OrderDealVO;

import java.math.BigDecimal;


/**
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-26 17:51:54
 */
public interface OrderDealService extends ICommonService<OrderDeal, Integer> {


    void create(String orderNo, Integer userId, Integer type, String remark, String... fileUrls);


    void create(Order order,
                String title,
                BigDecimal refundMoney,
                int userId,
                String remark,
                String[] fileUrls);


    OrderDealVO findByUserAndOrderNo(Integer userId, String orderNo);

    /**
     * 查看陪玩师提交的验收结果
     *
     * @param orderNo
     * @return
     */
    OrderDealVO findOrderAcceptanceResult(String orderNo);
}
