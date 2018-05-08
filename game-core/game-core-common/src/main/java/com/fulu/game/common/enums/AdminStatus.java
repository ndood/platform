package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminStatus implements TypeEnum<Integer> {

    DISABLE(0,"失效"),
    ENABLE(1,"启用");

    private Integer type;
    private String msg;
}
