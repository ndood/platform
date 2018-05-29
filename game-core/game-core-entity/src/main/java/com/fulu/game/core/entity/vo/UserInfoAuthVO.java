package com.fulu.game.core.entity.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.UserInfoAuthFile;
import com.fulu.game.core.entity.UserInfoFile;
import lombok.Data;

import java.util.List;


/**
 * 信息认证表
 *
 * @author wangbin
 * @date 2018-04-20 11:12:13
 */
@Data
public class UserInfoAuthVO extends UserInfoAuth {

    //真实姓名
    private String realname;

    private String nickname;

    private String realMobile;

    private Integer age;

    private String city;
    //性别
    private Integer gender;
    //身份证号
    private String idCard;
    //身份证人像
    @JsonIgnore
    private String idCardHeadUrl;
    //身份证国徽
    @JsonIgnore
    private String idCardEmblemUrl;
    //身份证手持
    @JsonIgnore
    private String idCardHandUrl;
    //头像
    private String headUrl;
    //写真URL集合
    @JsonIgnore
    private String[] portraitUrls;
    //声音URL
    @JsonIgnore
    private String voiceUrl;
    //标签   标签组ID加标签ID [1|3,1|2]
    @JsonIgnore
    private Integer[] tags;
    //身份证图片
    private List<UserInfoFile> idCardList;
    //写真文件
    private List<UserInfoAuthFile> portraitList;
    //声音文件
    private List<UserInfoAuthFile> voiceList;
    //标签组
    private List<TagVO> groupTags;

    private String remark;

    @JsonIgnore
    private String startTime;
    @JsonIgnore
    private String endTime;

}
