package com.fulu.game.core.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户表
 *
 * @author wangbin
 * @date 2018-04-20 11:12:12
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    @Excel(name = "用户ID", orderNum = "0", width = 15)
    private Integer id;
    //手机号
    @Excel(name = "手机号", orderNum = "1", width = 15)
    private String mobile;
    //昵称
    @Excel(name = "昵称", orderNum = "2", width = 15)
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
    @Excel(name = "类型", orderNum = "3", replace = {"玩家_1", "陪玩师_2", "渠道商_3"}, width = 15)
    private Integer type;
    //信息认证(0未完善,1已完善,2审核通过)
    @Excel(name = "认证状态", orderNum = "4", replace = {"未完善_1", "已完善_2", "审核通过_3"}, width = 15)
    private Integer userInfoAuth;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String salt;
    @Excel(name = "ImId", orderNum = "7", width = 30)
    private String imId;
    private String imPsw;
    @Excel(name = "来源Id", orderNum = "8", width = 15)
    private Integer sourceId;
//    @JsonIgnore
    private BigDecimal balance;
    //充值零钱（不可提现）
    private BigDecimal chargeBalance;
    //虚拟零钱（对应钻石数量）
    private Integer virtualBalance;
    //魅力值
    private Integer charm;
    //累计总提现魅力值
    private Integer charmDrawSum;
    //状态(0封禁,1为解封)
    @Excel(name = "用户状态", orderNum = "5", replace = {"封禁_false", "解封_true"}, width = 15)
    private Integer status;

    @JsonIgnore
    @Excel(name = "openId", orderNum = "6", width = 35)
    private String openId;

    @JsonIgnore
    private String pointOpenId;

    @JsonIgnore
    private String publicOpenId;

    @JsonIgnore
    private String unionId;

    //综合得星评分数
    private BigDecimal scoreAvg;
    //用户总积分
    private Integer userScore;
    //注册ip
    private String registIp;
    //登录ip
    private String loginIp;
    //注册时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "注册时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "9", width = 35)
    private Date createTime;
    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    //最后登录时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "最后登录登录时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "10", width = 35)
    private Date loginTime;

}
