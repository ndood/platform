package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlatformBannerEnum implements TypeEnum<Integer>{

    PLAY(1, "小程序首页"),
    APP(2, "APP首页"),
    APP_CHAT_ROOM(5, "APP聊天室");

    private Integer type;
    private String msg;
}
