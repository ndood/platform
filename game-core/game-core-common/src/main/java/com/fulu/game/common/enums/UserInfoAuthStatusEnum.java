package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.omg.CORBA.PUBLIC_MEMBER;

@AllArgsConstructor
@Getter
public enum UserInfoAuthStatusEnum implements TypeEnum<Integer>{

    NOT_PERFECT(0,"不通过"),
    ALREADY_PERFECT(1,"审核中"),
    VERIFIED(2,"审核通过"),
    FREEZE(3,"冻结");

    private Integer type;
    private String msg;

    public static String getMsgByType(Integer type){
        for(UserInfoAuthStatusEnum authStatusEnum : UserInfoAuthStatusEnum.values()){
            if(authStatusEnum.getType().equals(type)){
                return authStatusEnum.getMsg();
            }
        }
        return null;
    }

}
