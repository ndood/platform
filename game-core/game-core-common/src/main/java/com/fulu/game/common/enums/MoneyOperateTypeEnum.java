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
    ORDER_COMPLETE(2, "陪玩订单完成"),
    ADMIN_REFUSE_REMIT(3, "管理员拒绝打款"),
    WITHDRAW_VIRTUAL_MONEY(5, "余额兑换钻石"),
    WITHDRAW_BALANCE(6, "余额充值"),
    ORDER_REFUND(7, "订单余额退款");


    private Integer type;
    private String msg;

    public static String getMsgByType(Integer type) {
        for (MoneyOperateTypeEnum typeEnum : MoneyOperateTypeEnum.values()) {
            if (typeEnum.type.equals(type)) {
                return typeEnum.msg;
            }
        }
        return null;
    }
}