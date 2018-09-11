package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MoneyOperateTypeEnum implements TypeEnum<Integer> {
    //用户提现
    USER_DRAW_CASH(-1, "提现"),
    //管理员加零钱
    ADMIN_ADD_CHANGE(1, "加零钱"),
    //陪玩订单完成
    ORDER_COMPLETE(2, "陪玩订单"),
    ADMIN_REFUSE_REMIT(3, "管理员拒绝打款"),
    USER_CHARM_WITHDRAW(4, "魅力值提现"),
    WITHDRAW_VIRTUAL_MONEY(5, "余额购买虚拟币"),
    WITHDRAW_BALANCE(6, "余额充值"),

    CHANNEL_ADD_CASH(1, "渠道商加款"),
    CHANNEL_CUT_CASH(2, "渠道商扣款"),
    CHANNEL_REFUND(3, "渠道商退款"),
    CHANNEL_ADMIN_CUT(4, "管理员扣款");

    private Integer type;
    private String msg;
}