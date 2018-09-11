package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlatformBannerEnum implements TypeEnum<Integer>{

    PLAY(1, "小程序"),
    APP(2, "APP");

    private Integer type;
    private String msg;
}
