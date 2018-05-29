package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagTypeEnum implements TypeEnum<Integer> {

    PERSON(1, "个人标签"),
    GAME(2, "游戏标签");

    private Integer type;
    private String msg;

}
