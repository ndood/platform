package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.PilotOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 
 *
 * @author wangbin
 * @date 2018-06-26 14:44:22
 */
@Data
public class PilotOrderVO  extends PilotOrder {

    private Date completeTime;

    private BigDecimal price;

    private String unit;

    private String amount;

    private BigDecimal actualMoney;

    private BigDecimal totalMoney;

    private BigDecimal serverMoney;

    private BigDecimal commissionMoney;

    private Date createTime;

}
