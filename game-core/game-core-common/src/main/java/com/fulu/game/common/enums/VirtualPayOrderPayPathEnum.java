package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 充值订单表充值路径枚举类
 *
 * @author Gong ZeChun
 * @date 2018/9/13 17:16
 */
@Getter
@AllArgsConstructor
public enum VirtualPayOrderPayPathEnum implements TypeEnum<Integer> {
    MP(1, "公众号");

    private Integer type;
    private String msg;
}
