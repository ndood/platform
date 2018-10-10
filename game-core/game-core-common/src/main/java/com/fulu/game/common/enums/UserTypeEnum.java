package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  UserTypeEnum implements TypeEnum<Integer>{

    GENERAL_USER(1,"普通用户"),
    ACCOMPANY_PLAYER(2,"陪玩师"),
    CHANNEL_MERCHANT(3,"渠道商"),
    VEST_USER(4,"马甲");

    private Integer type;
    private String msg;


}
