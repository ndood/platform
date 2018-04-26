package com.fulu.game.core.service;

import com.fulu.game.common.enums.DetailsEnum;
import com.fulu.game.core.entity.OrderMoneyDetails;


/**
 * 订单流水表
 *
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-26 14:07:31
 */
public interface OrderMoneyDetailsService extends ICommonService<OrderMoneyDetails, Integer> {


    void create(String orderNo, Integer userId, DetailsEnum desc, String money);

}
