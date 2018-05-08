package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MoneyOperateTypeEnum implements TypeEnum<Integer>{

    USER_DRAW_CASH(-1, "用户提款"),
    ADMIN_ADD_CHANGE(1, "管理员加零钱"),
    ORDER_COMPLETE(2, "陪玩订单完成");

    private Integer type;
    private String msg;
}
