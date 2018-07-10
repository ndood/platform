package com.fulu.game.core.entity.to;

import com.fulu.game.core.entity.UserInfoAuth;
import lombok.Data;

/**
 * 接收用户认证的个人信息
 */
@Data
public class UserInfoAuthTO extends UserInfoAuth {

    //性别
    private Integer gender;
    //年龄
    private String age;
    //个人写真
    private String[] portraitUrls;
    //声音URL
    private String voiceUrl;
    //声音时长
    private Integer duration;
    //标签
    private Integer[] tags;


}
