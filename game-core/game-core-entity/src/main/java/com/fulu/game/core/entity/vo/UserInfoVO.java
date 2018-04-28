package com.fulu.game.core.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoVO {

    private Integer userId;

    private String nickName;

    private String headUrl;

    private String mainPhotoUrl;



    private Integer age;

    private Integer gender;

    private String city;

    private String voice;

    List<String> tags;

    //陪玩师照片集
    private List<String> photos;


}
