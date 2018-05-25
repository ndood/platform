package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TechAuthStatusEnum implements TypeEnum<Integer>{

    AUTHENTICATION_ING(1,"认证中"),
    NORMAL(2,"正常"),
    FREEZE(3,"冻结");

    private Integer type;
    private String msg;
}
