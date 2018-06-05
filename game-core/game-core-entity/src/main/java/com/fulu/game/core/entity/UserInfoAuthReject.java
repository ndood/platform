package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 用户认证信息驳回表
 * 
 * @author wangbin
 * @date 2018-05-28 20:04:35
 */
@Data
public class UserInfoAuthReject implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户ID
	private Integer userId;
	//用户信息认证ID
	private Integer userInfoAuthId;
	//信息认证状态
	private Integer userInfoAuthStatus;
	//原因
	private String reason;
	//管理员ID
	private Integer adminId;
	//管理员名称
	private String adminName;
	//创建时间
	private Date createTime;

}
