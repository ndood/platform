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
    //头像URL
    private String headPortraitsUrl;
    private Integer age;
    //密码
    @JsonIgnore
    private String password;
    //密码盐
    @JsonIgnore
    private String salt;
    //真实姓名
    private String realname;
    //身份证
    private String idcard;
    //1:普通用户,2:打手,3:渠道商
    @Excel(name = "类型", orderNum = "3", replace = {"玩家_1", "陪玩师_2", "渠道商_3"}, width = 15)
    private Integer type;
    //信息认证(0未完善,1已完善,2审核通过)
    @Excel(name = "认证状态", orderNum = "4", replace = {"未完善_1", "已完善_2", "审核通过_3"}, width = 15)
    private Integer userInfoAuth;
    //零钱
    @JsonIgnore
    private BigDecimal balance;
    //状态(0封禁,1为解封)
    @Excel(name = "用户状态", orderNum = "5", replace = {"封禁_false", "解封_true"}, width = 15)
    private Integer status;

    @JsonIgnore
    @Excel(name = "openId", orderNum = "6", width = 35)
    private String openId;
    private String city;
    private String province;
    private String country;
    //综合得星评分数
    private BigDecimal scoreAvg;
    //星座
    private String constellation;
    //生日
    private String birth;
    @Excel(name = "ImId", orderNum = "7", width = 30)
    private String imId;
    private String imPsw;
    @Excel(name = "来源Id", orderNum = "8", width = 15)
    private Integer sourceId;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "注册时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "9", width = 35)
    private Date createTime;
    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
