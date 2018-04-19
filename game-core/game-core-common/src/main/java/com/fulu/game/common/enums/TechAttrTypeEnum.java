package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TechAttrTypeEnum implements TypeEnum<Integer>{

    SALES_MODE(1,"个人标签"),
    DAN(2,"游戏标签");

    private Integer type;
    private String msg;
}
