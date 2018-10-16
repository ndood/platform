package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;




public enum RoomEnum{

    ;

    @Getter
    @AllArgsConstructor
    public   enum  RoomRoleTypeEnum implements TypeEnum<Integer>{
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


    @Getter
    @AllArgsConstructor
    public enum  RoomMicRankEnum implements TypeEnum<Integer>{
        ORDER(1, "点单"),
        AUDITION(2, "试音");
        private Integer type;
        private String msg;
    }


    @Getter
    @AllArgsConstructor
    public enum  RoomTemplateEnum implements TypeEnum<Integer>{
        ORDER(1, "派单房"),
        RECREATION(2, "娱乐房");
        private Integer type;
        private String msg;
    }

    @Getter
    @AllArgsConstructor
    public enum  RoomOrderEnum implements TypeEnum<Integer>{
        ORDER(1, "陪玩订单"),
        GIFT(2, "礼物");
        private Integer type;
        private String msg;
    }




}



