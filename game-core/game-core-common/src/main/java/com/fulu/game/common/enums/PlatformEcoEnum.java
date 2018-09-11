package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlatformEcoEnum implements TypeEnum<Integer> {


    PLAY(1, "开黑陪玩"),
    POINT(2, "开黑上分"),
    MP(3, "微信公众号"),
    IOS(4, "IOS"),
    ANDROID(5, "ANDROID"),
    APP(45, "APP(android+ios)");


    private Integer type;
    private String msg;

}
