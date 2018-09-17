package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  PayBusinessEnum implements TypeEnum<Integer>{


    ORDER(1,"订单业务"),
    VIRTUAL_PRODUCT(2,"虚拟币业务");

    private Integer type;
    private String msg;

}
