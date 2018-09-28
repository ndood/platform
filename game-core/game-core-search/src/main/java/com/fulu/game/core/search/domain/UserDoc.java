package com.fulu.game.core.search.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.searchbox.annotations.JestId;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.BitSet;
import java.util.Date;
import java.util.List;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/24 14:37.
 * @Description: 动态DOC
 */
@Data
@ToString
public class UserDoc {
    @JestId
    private Integer id;
    //手机号
    private String mobile;
    //昵称
    private String nickname;
    //性别(0不公开,1男,2女)
    private Integer gender;

    private Integer age;

    private String constellation;

    private String birth;

    private String realname;

    private String country;

    private String province;

    private String city;
    //头像URL
    private String headPortraitsUrl;
    //身份证
    private String idcard;
    //1:普通用户,2:打手,3:渠道商
    private Integer type;
    //信息认证(0未完善,1已完善,2审核通过)
    private Integer userInfoAuth;
    //状态(0封禁,1为解封)
    private Integer status;

}
