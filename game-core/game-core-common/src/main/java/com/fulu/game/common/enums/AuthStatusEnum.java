package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  AuthStatusEnum implements TypeEnum<Integer>{

    NOT_PERFECT(0,"未完善"),
    ALREADY_PERFECT(1,"已完善"),
    VERIFIED(2,"审核通过"),
    REJECT(3,"审核不通过");

    private Integer type;
    private String msg;

}
