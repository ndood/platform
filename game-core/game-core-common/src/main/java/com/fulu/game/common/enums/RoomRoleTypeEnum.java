package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  RoomRoleTypeEnum implements TypeEnum<Integer>{


    OWNER(1, "房主"),
    MANAGE(2, "管理"),
    PRESIDE(3, "主持");

    private Integer type;
    private String msg;


    public static   RoomRoleTypeEnum findByType(Integer type){
        for(RoomRoleTypeEnum roleTypeEnum : RoomRoleTypeEnum.values()){
            if(roleTypeEnum.getType().equals(type)){
                return roleTypeEnum;
            }
        }
        return null;
    }

}
