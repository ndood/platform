package com.fulu.game.core.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoomMicVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //麦位号
    private Integer index;
    //麦位状态
    private Integer status;
    //麦位用户
    private MicUser micUser;


    //麦位用户
    public static class MicUser{

        private Integer userId;

        private String headPortraitsUrl;

        private String nickname;

        private Integer gender;

        private Integer roomRole;
    }

}
