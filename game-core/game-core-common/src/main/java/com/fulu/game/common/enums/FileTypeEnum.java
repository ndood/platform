package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileTypeEnum implements TypeEnum<Integer>{

    PIC(1,"写真图片"),
    VOICE(2,"声音"),
    MAIN_PIC(3, "主图"),
    VIDEO(4, "视频");
    private Integer type;
    private String msg;

}
