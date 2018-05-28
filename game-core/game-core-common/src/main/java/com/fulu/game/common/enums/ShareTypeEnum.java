package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShareTypeEnum implements TypeEnum<Integer> {

    ORDER_SET(1, "接单设置"),
    TECH_AUTH(2, "技能认证"),
    USER_CARD(3, "名片");
    private Integer type;
    private String msg;
}
