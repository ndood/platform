package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TechAuthStatusEnum implements TypeEnum<Integer>{

    AUTHENTICATION_ING(0,"认证中"),
    NORMAL(1,"正常"),
    FREEZE(2,"冻结");

    private Integer type;
    private String msg;
}
