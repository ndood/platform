package com.fulu.game.core.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class WxUserInfo implements Serializable {
    private String nickName;
    private String mobile;
    private String avatarUrl;
    private Integer gender;
    private String city;
    private String province;
    private String country;
    private String language;
    private String verifyCode;
}
