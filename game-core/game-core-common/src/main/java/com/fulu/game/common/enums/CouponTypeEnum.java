package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CouponTypeEnum implements  TypeEnum<Integer>{

    DERATE(1,"减额"),
    DISCOUNT(2,"折扣");

    private Integer type;
    private String msg;
}
