package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagTypeEnum implements TypeEnum<Integer>{

    PERSON(1,"个人标签"),
    GAME(2,"游戏标签"),

    FACE(101,"外貌标签"),
    VOICE(102,"声音标签"),
    TECH(103,"技术标签");

    private Integer type;
    private String msg;

}
