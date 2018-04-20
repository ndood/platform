package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileTypeEnum implements TypeEnum<Integer>{

    PIC(1,"图片"),
    VOICE(2,"声音");

    private Integer type;
    private String msg;

}
