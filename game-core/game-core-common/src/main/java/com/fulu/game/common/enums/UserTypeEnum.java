package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  UserTypeEnum implements TypeEnum<Integer>{

    GENERAL_USER(1,"普通用户"),
    ACCOMPANY_PLAYER(2,"陪玩师"),
    CHANNEL_MERCHANT(3,"普通用户");

    private Integer type;
    private String msg;


}
