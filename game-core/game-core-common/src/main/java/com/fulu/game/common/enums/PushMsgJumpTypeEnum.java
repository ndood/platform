package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PushMsgJumpTypeEnum implements TypeEnum<Integer> {
    H5(1, "H5"),
    PAGE(2, "内部跳转页(小程序）"),
    OFFICIAL_NOTICE(3, "官方公告"),
    CHAT_ROOM(4, "聊天室"),
    CARD_PAGE(5, "名片页"),
    INDEX_PAGE(6, "APP首页");

    private Integer type;
    private String msg;


    public static PushMsgJumpTypeEnum convert(Integer jumpType){
         for(PushMsgJumpTypeEnum jumpTypeEnum : PushMsgJumpTypeEnum.values()){
             if(jumpTypeEnum.getType().equals(jumpType)){
                 return jumpTypeEnum;
             }
         }
         return INDEX_PAGE;
    }


}
