package com.fulu.game.core.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Data
public class RoomMicVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @AllArgsConstructor
    @Getter
    public enum MicStatus{
        OPEN(1),//开麦
        CLOSE(2),//闭麦
        SHUTUP(3);//禁言
        private Integer status;
    }

    //麦位号
    private Integer index;
    //麦位状态
    private Integer status;
    //麦位用户
    private MicUser micUser;


    //麦位用户
    @Data
    public static class MicUser{

        private Integer userId;

        private String headPortraitsUrl;

        private String nickname;

        private Integer gender;

        private Integer age;

        private Integer roomRole;
    }

}
