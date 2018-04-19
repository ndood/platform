package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TechAttrTypeEnum implements TypeEnum<Integer>{

    SALES_MODE(1,"销售方式"),
    DAN(2,"段位");

    private Integer type;
    private String msg;
}
