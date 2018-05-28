package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserInfoAuthStatusEnum implements TypeEnum<Integer>{

    NOT_PERFECT(0,"不通过"),
    ALREADY_PERFECT(1,"已完善"),
    VERIFIED(2,"审核通过"),
    FREEZE(3,"冻结");

    private Integer type;
    private String msg;

}
