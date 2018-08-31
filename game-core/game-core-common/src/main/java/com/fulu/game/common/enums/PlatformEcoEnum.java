package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlatformEcoEnum implements TypeEnum<Integer> {


    PLAY(1, "开黑陪玩"),
    POINT(2, "开黑上分"),
    H5(3, "IOS"),
    IOS(4, "ANDROID"),
    ANDROID(5, "IOS"),
    APP(45, "APP(android+ios)");


    private Integer type;
    private String msg;

}
