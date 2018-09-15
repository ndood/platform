package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提现类型枚举类
 *
 * @author Gong ZeChun
 * @date 2018/9/11 1:34
 */
@Getter
@AllArgsConstructor
public enum CashDrawsTypeEnum implements TypeEnum<Integer> {
    BALANCE_WITHDRAW(1, "余额提现"),
    CHARM_WITHDRAW(2, "魅力值提现");

    private Integer type;
    private String msg;

    public static String getMsgByType(Integer type) {
        for (CashDrawsTypeEnum typeEnum : CashDrawsTypeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum.msg;
            }
        }
        return null;
    }
}
