package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderConsultTypeEnum implements TypeEnum<Integer> {

    APPEAL(1,"仲裁"),
    CHECK(2,"验收"),
    CONSULT(3,"协商");


    private Integer type;
    private String msg;
}
