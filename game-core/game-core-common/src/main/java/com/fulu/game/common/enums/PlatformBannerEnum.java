package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlatformBannerEnum implements TypeEnum<Integer>{

    PLAY(1, "小程序"),
    APP(2, "APP"),
    THUNDER_HOMEPAGE(3, "迅雷约玩-首页"),
    THUNDER_LIST(4, "迅雷约玩-列表页");

    private Integer type;
    private String msg;
}
