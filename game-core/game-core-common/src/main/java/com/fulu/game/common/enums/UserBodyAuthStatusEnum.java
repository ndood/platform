package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserBodyAuthStatusEnum implements TypeEnum<Integer> {

    NO_AUTH(0, "未审核"),
    AUTH_SUCCESS(1, "审核通过"),
    AUTH_FAIL(2, "审核未通过");

    private Integer type;
    private String msg;
}
