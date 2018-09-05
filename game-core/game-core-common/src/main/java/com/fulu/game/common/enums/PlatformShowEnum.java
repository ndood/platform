package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlatformShowEnum implements TypeEnum<String>{

    PLAY(1, "开黑陪玩"),
    APP(2, "APP"),
    PLAY_APP(3, "小程序和APP");

    private Integer type;
    private String msg;
}
