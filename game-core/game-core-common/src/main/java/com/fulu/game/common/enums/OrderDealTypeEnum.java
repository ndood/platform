package com.fulu.game.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  OrderDealTypeEnum implements  TypeEnum<Integer>{

    APPEAL(1,"申诉"),
    CHECK(2,"验收");

    private Integer type;
    private String msg;
}
