package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatusEnum  implements TypeEnum<Integer>{

    NORMAL(1,"正常"),
    FREEZE(2,"冻结"),
    BANNED(3,"封禁");

    private Integer type;
    private String msg;
}
