package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusEnum implements TypeEnum<String>{

    ADVICE_WAIT(0,"待处理"),
    ADVICE_MARKED(1,"标记"),
    ADVICE_SOLVED(2,"已处理");

    private Integer type;
    private String msg;
}
