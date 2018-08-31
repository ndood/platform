package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  PushMsgTypeEnum implements TypeEnum<Integer>{

    ALL_USER(1, "全体"),
    ASSIGN_USERID(2, "指定用户ID"),
    ALL_SERVICE_USER(3, "所有陪玩师");

    private Integer type;
    private String msg;
}
