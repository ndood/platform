package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  OrderTypeEnum  implements TypeEnum<Integer>{


    PLATFORM(1,"平台订单"),
    MARKET(2,"市场订单");

    private Integer type;
    private String msg;

}
