package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PagePathEnum {

    //todo 未发布的页面请求小程序码报错，后期需要配置
    TECH_SHARE_CARD("pages/index/index","陪玩师技能名片loadpage"),
    TECH_AUTH_CARD("pages/index/index","技能认证分享loadpage");

    //跳转页面路径
    private String pagePath;
    //路径描述
    private String content;
}
