package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  PushMsgTypeEnum implements TypeEnum<Integer>{

    ALL_USER(1, "全体"),
    ASSIGN_USERID(2, "指定用户ID");

    private Integer type;
    private String msg;
}
