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
    FENQILE(6, "分期乐"),
    THUNDER(7, "迅雷"),
    APP(45, "APP(android+ios)");


    private Integer type;
    private String msg;


    public static PlatformEcoEnum getEnumByType(Integer type) {
        for (PlatformEcoEnum platformEcoEnum : PlatformEcoEnum.values()) {
            if (platformEcoEnum.getType().equals(type)) {
                return platformEcoEnum;
            }
        }
        throw new IllegalArgumentException("{" + type + "}平台不匹配");
    }


    public static String getMsgByType(Integer type) {
        for (PlatformEcoEnum ecoEnum : PlatformEcoEnum.values()) {
            if (ecoEnum.type.equals(type)) {
                return ecoEnum.msg;
            }
        }
        return null;
    }
}
