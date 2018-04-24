package com.fulu.game.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  UserInfoFileTypeEnum implements TypeEnum<Integer>{

    IDCARD_HEAD(1,"身份证人像面"),
    IDCARD_EMBLEM(2,"身份证国徽面"),
    IDCARD_HAND(3,"手持身份证照片");

    private Integer type;
    private String msg;

}
