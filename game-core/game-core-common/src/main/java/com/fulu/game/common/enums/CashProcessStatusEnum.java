package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CashProcessStatusEnum implements TypeEnum<Integer> {

    WAITING(0, "待打款"),
    DONE(1, "已打款"),
    REFUSE(2, "已拒绝");

    private Integer type;
    private String msg;

}
