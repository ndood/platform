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
     * 外链来源id
     */
    private Integer sourceId;

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

    /**
     * 陪玩师是否是CJ来源陪玩师（1：是，0：否）
     */
    private Integer isCjPlayer;
}
