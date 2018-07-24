package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.OrderPointProduct;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 上分订单详情
 *
 * @author wangbin
 * @date 2018-07-24 17:55:45
 */
@Data
public class OrderPointProductVO extends OrderPointProduct {

    private String pointTypeStr;

    private BigDecimal totalMoney;
}
