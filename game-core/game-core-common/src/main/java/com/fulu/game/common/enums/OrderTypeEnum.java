package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  OrderTypeEnum  implements TypeEnum<Integer>{


    PLATFORM(1,"陪玩订单"),
    POINT(2,"上分订单");

    private Integer type;
    private String msg;

}
