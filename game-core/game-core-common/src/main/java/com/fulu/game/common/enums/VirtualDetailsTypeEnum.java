package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 虚拟货币和魅力值流水表-种类枚举类
 *
 * @author Gong ZeChun
 * @date 2018/8/30 20:02
 */
@Getter
@AllArgsConstructor
public enum VirtualDetailsTypeEnum implements TypeEnum<Integer> {
    VIRTUAL_MONEY(1, "虚拟币"),
    CHARM(2, "魅力值");

    private Integer type;
    private String msg;
}
