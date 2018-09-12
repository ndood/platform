package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 虚拟币和余额充值订单表--订单类型枚举类
 *
 * @author Gong ZeChun
 * @date 2018/9/5 18:04
 */
@Getter
@AllArgsConstructor
public enum VirtualPayOrderTypeEnum implements TypeEnum<Integer> {
    VIRTUAL_ORDER(1, "虚拟币充值订单"),
    BALANCE_ORDER(2, "余额充值订单");

    private Integer type;
    private String msg;
}
