package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TechAuthStatusEnum implements TypeEnum<Integer>{
    NO_AUTHENTICATION(0,"未通过"),
    AUTHENTICATION_ING(1,"认证中"),
    NORMAL(2,"已通过"),
    FREEZE(3,"冻结");

    private Integer type;
    private String msg;


    public static String getMsgByType(Integer type){
        for(TechAuthStatusEnum authStatusEnum : TechAuthStatusEnum.values()){
            if(authStatusEnum.getType().equals(type)){
                return authStatusEnum.getMsg();
            }
        }
        return null;
    }



}
