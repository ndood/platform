package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户枚举类系方式
 *
 * @author Gong ZeChun
 * @date 2018/7/27 14:24
 */
@Getter
@AllArgsConstructor
public enum ContactTypeEnum implements TypeEnum<Integer> {
    MOBILE(1, "手机号"),
    QQ(2, "QQ号"),
    WE_CHAT(3, "微信号");

    private Integer type;
    private String msg;

}
