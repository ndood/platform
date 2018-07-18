package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderConsultTypeEnum implements TypeEnum<Integer> {

    CONSULT(1, "协商"),
    ARBITRATE(2, "仲裁");


    private Integer type;
    private String msg;
}
