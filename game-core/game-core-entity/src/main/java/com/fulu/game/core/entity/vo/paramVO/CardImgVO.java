package com.fulu.game.core.entity.vo.paramVO;

import lombok.Data;

import java.util.Map;

@Data
public class CardImgVO {
    private String nickname;
    private Integer gender;
    private Integer age;
    private String city;

    public CardImgVO setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public CardImgVO setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    private String codeUrl;
    private String mainPicUrl;
    private String personTagStr;
    //技能分享的文案
    private String shareStr;
    //技能认证的文案
    private String share_title;
    private String share_content;
    //主技能/主商品
    private Map<String ,String> mainTech;
    //第二商品
    private Map<String ,String> secTech;
}
