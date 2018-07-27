package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointTypeEnum implements TypeEnum{



    PARTY(1, "开黑"),
    WIN(2, "包赢"),
    ACCURATE_SCORE(3, "精准上分");

    private Integer type;
    private String msg;




    public static String getMsgByType(Integer type) {
        for (PointTypeEnum toWinTypeEnum : PointTypeEnum.values()) {
            if (toWinTypeEnum.getType().equals(type)) {
                return toWinTypeEnum.msg;
            }
        }
        return null;
    }
}
