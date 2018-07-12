package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MoneyOperateTypeEnum implements TypeEnum<Integer>{

    USER_DRAW_CASH(-1, "用户提款"),
    ADMIN_ADD_CHANGE(1, "管理员加零钱"),
    ORDER_COMPLETE(2, "陪玩订单完成"),
    ADMIN_REFUSE_REMIT(3, "管理员拒绝打款"),

    CHANNEL_ADD_CASH(1, "渠道商加款"),
    CHANNEL_CUT_CASH(2, "渠道商扣款"),
    CHANNEL_REFUND(3, "渠道商退款"),
    CHANNEL_ADMIN_CUT(4, "管理员扣款");

    private Integer type;
    private String msg;
}
