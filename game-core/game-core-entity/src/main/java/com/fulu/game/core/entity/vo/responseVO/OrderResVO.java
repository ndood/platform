package com.fulu.game.core.entity.vo.responseVO;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderMarketProduct;
import com.fulu.game.core.entity.OrderProduct;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.OrderDealVO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 2018.4.29
 * 管理员订单列表查询结果VO
 */
@Data
public class OrderResVO extends Order {


    private User user;//玩家
    private User serverUser;//陪玩师

    private OrderDealVO userOrderDeal;//订单存在异议信息
    private OrderDealVO serverOrderDeal;//订单提交验收信息

    private OrderProduct orderProduct; //订单商品

//    private OrderMarketProduct orderMarketProduct; //集市订单商品

    private BigDecimal commissionMoney;

    private String statusStr;


}
