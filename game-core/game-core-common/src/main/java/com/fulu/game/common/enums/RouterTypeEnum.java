package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RouterTypeEnum implements TypeEnum<Integer> {

    MENU(1,"菜单"),
    PERMISSION(2,"权限");

    private Integer type;
    private String msg;
}
