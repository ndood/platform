package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 迅雷兑换码枚举类
 *
 * @author Gong ZeChun
 * @date 2018/10/12 9:56
 */
@Getter
@AllArgsConstructor
public enum ThunderCodeEnum implements TypeEnum<Integer> {
    //类型（1：搜狐；2：喜马拉雅；3：steam游戏CDK；4：steam账号和密码；5：CIBN会员）
    SOHU(1, "搜狐"),
    XIMALAYA(2, "喜马拉雅"),
    STEAM_SDK(3, "steam游戏CDK"),
    STEAM_ACCOUNT_PSW(4, "steam账号和密码"),
    CIBN(5, "CIBN会员");

    private Integer type;
    private String msg;
}
