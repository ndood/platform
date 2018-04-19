package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryParentEnum implements TypeEnum<Integer>{


    ACCOMPANY_PLAY(1, "陪玩");

    private Integer type;
    private String msg;

}
