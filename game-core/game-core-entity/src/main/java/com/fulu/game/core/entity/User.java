package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 用户表
 * 
 * @author wangbin
 * @date 2018-04-20 11:12:12
 */
@Data
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//手机号
	private String mobile;
	//昵称
	private String nickname;
	//性别(1男,2女)
	private Integer gender;
	//头像URL
	private String headPortraitsUrl;
	//密码
	private String password;
	//密码盐
	private String salt;
	//真实姓名
	private String realname;
	//身份证
	private String idcard;
	//1:普通用户,2:打手,3:渠道商
	private Integer type;
	//信息认证(0未完善,1已完善,2审核通过)
	private Integer userInfoAuth;
	//零钱
	private BigDecimal balance;
	//状态
	private Integer status;
	//创建时间
	private Date createTime;
	//修改时间
	private Date updateTime;

}
