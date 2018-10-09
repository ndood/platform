package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  RoomRoleTypeEnum implements TypeEnum<Integer>{


    owner(1, "手机号"),
    manage(2, "QQ号"),
    preside(3, "微信号");

    private Integer type;
    private String msg;
}
