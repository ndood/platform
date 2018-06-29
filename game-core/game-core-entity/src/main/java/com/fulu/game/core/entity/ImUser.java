package com.fulu.game.core.entity;

import lombok.Data;

@Data
public class ImUser {
    private String uuid;
    private String type;
    private String created;
    private String modified;
    //环信id
    private String username;
    //环信密码
    private String imPsw;

    private Boolean activated;
    //用户id
    private Integer userId;

}
