package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SettingTypeEnum implements TypeEnum<Integer>{


    AUTO_RECEIVE_ORDER_TIME(1, "自动接单时间设置");

    private Integer type;
    private String msg;
}
