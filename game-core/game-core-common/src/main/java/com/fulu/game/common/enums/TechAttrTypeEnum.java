package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TechAttrTypeEnum implements TypeEnum<Integer>{

    DAN(2,"段位"),
    AREA(3,"大区");

    private Integer type;
    private String msg;
}
