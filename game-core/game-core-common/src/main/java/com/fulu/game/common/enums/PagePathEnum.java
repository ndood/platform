package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PagePathEnum {

    TECH_SHARE_CARD("pages/c/card/card","陪玩师技能名片loadpage"),
    TECH_AUTH_CARD("pages/a/card/card","技能认证分享loadpage");

    //跳转页面路径
    private String pagePath;
    //路径描述
    private String content;
}
