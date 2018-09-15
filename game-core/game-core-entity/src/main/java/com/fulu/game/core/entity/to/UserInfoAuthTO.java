package com.fulu.game.core.entity.to;

import com.fulu.game.core.entity.UserInfoAuth;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 接收用户认证的个人信息
 */
@Data
public class UserInfoAuthTO extends UserInfoAuth {

    //性别
    private Integer gender;

    private String headUrl;


    private String birth;

    private String constellation;
    //年龄
    private Integer age;
    //个人写真
    private String[] portraitUrls;
    //声音URL
    private String voiceUrl;
    //声音时长
    private Integer duration;
    //标签
    private Integer[] tags;
    //综合得星评分数
    private BigDecimal scoreAvg;

    private String privatePicStr;
}
