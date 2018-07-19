package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CashProcessStatusEnum implements TypeEnum<Integer> {

    WAITING(0, "提现(审核中)"),
    DONE(1, "提现(已完成)"),
    REFUSE(2, "提现(已拒绝)"),
    REFUND(3, "提现(金额退回)");

    private Integer type;
    private String msg;

}
