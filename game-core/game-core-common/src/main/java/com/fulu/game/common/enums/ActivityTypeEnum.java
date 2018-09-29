package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  ActivityTypeEnum implements TypeEnum<Integer>{

    COUPON(1,"优惠券"),
    LINK(2,"链接");

    private Integer type;
    private String msg;


}
