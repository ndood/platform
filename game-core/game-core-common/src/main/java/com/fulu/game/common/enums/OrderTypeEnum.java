package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  OrderTypeEnum  implements TypeEnum<Integer>{


    PLAY(1,"陪玩订单"),
    POINT(2,"上分订单");

    private Integer type;
    private String msg;

    public static String getMsgByType(Integer type) {
        for (OrderTypeEnum typeEnum : OrderTypeEnum.values()) {
            if (typeEnum.type.equals(type)) {
                return typeEnum.msg;
            }
        }
        return null;
    }
}
