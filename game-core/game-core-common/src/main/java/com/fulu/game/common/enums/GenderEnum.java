package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderEnum implements TypeEnum<Integer> {

    MALE(1, "男"),
    LADY(2, "女"),
    ASEXUALITY(0, "无性别"),
    SYMBOL_MALE(1,"♂"),
    SYMBOL_LADY(2,"♀");

    private Integer type;
    private String msg;


}
