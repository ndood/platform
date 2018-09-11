package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * todo：描述文字
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
}
