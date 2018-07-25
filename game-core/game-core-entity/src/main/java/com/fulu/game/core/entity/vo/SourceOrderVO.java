package com.fulu.game.core.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 外链来源订单
 *
 * @author Gong ZeChun
 * @date 2018/7/24 19:17
 */
@Data
public class SourceOrderVO {

    /**
     * 外链来源名称
     */
    private String sourceStr;

    /**
     * 外链来源完成订单数
     */
    private Integer sourceOrderCounts;

    /**
     * 陪玩师通过外链来源的服务总收入
     */
    private BigDecimal serverMoney;

    /**
     * 平台收入金额
     */
    private BigDecimal commissionMoney;
}
